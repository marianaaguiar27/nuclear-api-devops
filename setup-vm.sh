#!/usr/bin/env bash
# Executado DENTRO da VM (como root) pelo az vm run-command.
# O texto __REPO_URL__ e substituido pelo deploy-azure.sh.
set -euo pipefail

REPO_URL="__REPO_URL__"

echo ">> Aguardando a VM terminar a configuracao inicial..."
cloud-init status --wait || true

echo ">> Instalando git e curl..."
apt-get update -y
apt-get install -y git curl

echo ">> Instalando Docker..."
curl -fsSL https://get.docker.com | sh
systemctl enable --now docker
docker --version

echo ">> Baixando o codigo do repositorio..."
rm -rf /opt/nuclear-api
git clone --depth 1 "$REPO_URL" /opt/nuclear-api
cd /opt/nuclear-api

echo ">> Gerando a imagem a partir do Dockerfile..."
docker build -t nuclear-api:1.0 .

echo ">> Executando o container..."
docker rm -f nuclear-api 2>/dev/null || true
docker run -d --name nuclear-api --restart unless-stopped -p 8080:8080 nuclear-api:1.0

sleep 5
echo ">> Containers em execucao:"
docker ps
