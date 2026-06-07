#!/usr/bin/env bash
# =============================================================
# deploy.sh — Run this on your VPS to deploy / redeploy
# Usage:
#   chmod +x deploy.sh
#   ./deploy.sh            # first run or full redeploy
#   ./deploy.sh --update   # pull latest code + restart services
# =============================================================
set -euo pipefail

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPOSE="docker compose -f docker-compose.yml -f docker-compose.prod.yml"

# ── colours ──────────────────────────────────────────────────
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

# ── pre-flight checks ─────────────────────────────────────────
command -v docker  >/dev/null 2>&1 || error "Docker not found. Install it first."
command -v git     >/dev/null 2>&1 || error "Git not found."
[ -f "$APP_DIR/.env" ] || error ".env file not found. Copy .env.example → .env and fill in the values."

cd "$APP_DIR"

# ── pull latest code (if --update flag is passed) ─────────────
if [[ "${1:-}" == "--update" ]]; then
    info "Pulling latest code from git..."
    git pull
fi

# ── build images ──────────────────────────────────────────────
info "Building Docker images (this takes a few minutes on first run)..."
$COMPOSE build --parallel

# ── stop old containers gracefully ────────────────────────────
info "Stopping existing containers..."
$COMPOSE down --remove-orphans || true

# ── start everything ──────────────────────────────────────────
info "Starting all services..."
$COMPOSE up -d

# ── wait and show status ──────────────────────────────────────
info "Waiting 30s for services to initialise..."
sleep 30

info "Container status:"
$COMPOSE ps

echo ""
info "Deployment complete."
info "Tail logs with:  docker compose -f docker-compose.yml -f docker-compose.prod.yml logs -f"
info "Health check:    curl -s http://127.0.0.1:8080/actuator/health | python3 -m json.tool"
