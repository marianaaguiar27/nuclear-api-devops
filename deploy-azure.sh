#!/usr/bin/env bash
# =============================================================================
# deploy-azure.sh
# Cria uma VM no Azure via Azure CLI, instala o Docker nela e executa a
# API "Nuclear no Dia a Dia" em container. Ao final, testa a API pela internet.
#
# Uso:   ./deploy-azure.sh <URL_DO_REPOSITORIO> [TAMANHO_DA_VM] [REGIAO]
# Ex.:   ./deploy-azure.sh https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git Standard_B2as_v2 mexicocentral
# Opcional (4o argumento): zona de disponibilidade, ex.: 1
# Requisitos: Azure CLI instalado e login feito (az login). Repositorio publico.
# =============================================================================
set -euo pipefail

# ---------- Configuracoes ----------
REPO_URL="${1:-https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git}"
RG="rg-nuclear-api"
LOCATION="${3:-mexicocentral}"
VM_NAME="vm-nuclear-api"
VM_SIZE="${2:-Standard_B2as_v2}"
VM_IMAGE="Ubuntu2204"
ADMIN_USER="azureuser"
APP_PORT=8080
ZONE="${4:-}"
ZONE_ARGS=()
if [[ -n "$ZONE" ]]; then ZONE_ARGS=(--zone "$ZONE"); fi

# Trabalha sempre na pasta do script (evita problemas de caminho no Windows)
cd "$(dirname "${BASH_SOURCE[0]}")"

if [[ "$REPO_URL" == *"SEU_USUARIO"* ]]; then
  echo "ERRO: informe a URL do repositorio GitHub."
  echo "Exemplo: ./deploy-azure.sh https://github.com/usuario/nuclear-api-java.git"
  exit 1
fi

echo "=== [1/6] Conta Azure em uso ==="
az account show --query "{Assinatura:name, Estado:state}" --output table

echo
echo "=== [2/6] Criando o grupo de recursos: $RG ($LOCATION) ==="
az group create --name "$RG" --location "$LOCATION" --output table

echo
echo "=== [3/6] Criando a VM $VM_NAME ($VM_SIZE, $VM_IMAGE) ==="
az vm create \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  --image "$VM_IMAGE" \
  --size "$VM_SIZE" \
  --admin-username "$ADMIN_USER" \
  --generate-ssh-keys \
  --public-ip-sku Standard \
  "${ZONE_ARGS[@]}" \
  --output table

echo
echo "=== [4/6] Liberando a porta $APP_PORT no firewall (NSG) da VM ==="
az vm open-port \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  --port "$APP_PORT" \
  --priority 1010 \
  --output none
echo "Porta $APP_PORT liberada."

echo
echo "=== [5/6] Instalando Docker na VM e subindo o container ==="
# Gera o script remoto com a URL do repositorio e executa dentro da VM
sed "s|__REPO_URL__|$REPO_URL|g" setup-vm.sh > setup-vm.tmp.sh
trap 'rm -f setup-vm.tmp.sh' EXIT
az vm run-command invoke \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  --command-id RunShellScript \
  --scripts @setup-vm.tmp.sh \
  --query "value[0].message" \
  --output tsv

echo
echo "=== [6/6] Comprovando o provisionamento e testando a API ==="
az vm show --show-details --resource-group "$RG" --name "$VM_NAME" \
  --query "{VM:name, Tamanho:hardwareProfile.vmSize, Regiao:location, Estado:powerState, IP:publicIps}" \
  --output table
echo
az resource list --resource-group "$RG" --query "[].{Recurso:name, Tipo:type}" --output table

IP=$(az vm show --show-details --resource-group "$RG" --name "$VM_NAME" --query publicIps --output tsv)

echo
echo "Aguardando a API responder em http://$IP:$APP_PORT ..."
OK=0
for i in $(seq 1 30); do
  if curl -sf "http://$IP:$APP_PORT/api/conteudos" -o /dev/null; then OK=1; break; fi
  sleep 5
done

if [[ "$OK" -eq 1 ]]; then
  echo "API NO AR!"
  echo "  Endpoint: http://$IP:$APP_PORT/api/conteudos"
  echo "  Swagger : http://$IP:$APP_PORT/swagger-ui.html"
  echo
  curl -s "http://$IP:$APP_PORT/api/conteudos?categoria=SAUDE"
  echo
else
  echo "A API nao respondeu a tempo. Verifique com:"
  echo "  az vm run-command invoke -g $RG -n $VM_NAME --command-id RunShellScript --scripts 'docker ps; docker logs nuclear-api'"
fi

echo
echo "Quando terminar, apague tudo com: ./cleanup-azure.sh"
