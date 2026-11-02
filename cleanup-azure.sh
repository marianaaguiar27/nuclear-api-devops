#!/usr/bin/env bash
# =============================================================================
# cleanup-azure.sh
# Apaga o grupo de recursos (VM, disco, IP, rede) e comprova a exclusao.
# =============================================================================
set -euo pipefail

RG="rg-nuclear-api"

echo "=== Grupos de recursos ANTES da exclusao ==="
az group list --query "[].{Grupo:name, Regiao:location}" --output table

echo
echo "=== Apagando o grupo $RG (pode levar alguns minutos) ==="
az group delete --name "$RG" --yes

echo
echo "=== Comprovacao ==="
echo -n "O grupo $RG ainda existe? "
az group exists --name "$RG"

echo
echo "=== Grupos de recursos DEPOIS da exclusao ==="
az group list --query "[].{Grupo:name, Regiao:location}" --output table
