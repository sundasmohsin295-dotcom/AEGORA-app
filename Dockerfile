# ==============================================================================
# AEGORA AUTONOMOUS SOC BACKEND: PRODUCTION MULTI-STAGE DOCKERFILE
# ==============================================================================
# Compliance: CIS Docker Benchmark, OWASP Container Security, DevOps Roadmap
# Compatible with: Railway, Render, Fly.io, Google Cloud Run
# ==============================================================================

# --- STAGE 1: Dependency Builder ---
FROM python:3.11-slim-bullseye AS builder

WORKDIR /build

# Install build dependencies securely
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements and build wheels
COPY backend/requirements.txt .
RUN pip install --no-cache-dir --user -r requirements.txt


# --- STAGE 2: Hardened Production Runtime ---
FROM python:3.11-slim-bullseye AS runner

# Security: Enforce non-root execution (UID 10001)
RUN groupadd -g 10001 aegora && \
    useradd -u 10001 -g aegora -s /bin/bash -m aegora

WORKDIR /app

# Install runtime PostgreSQL client libraries
RUN apt-get update && apt-get install -y --no-install-recommends \
    libpq5 \
    curl \
    iptables \
    && rm -rf /var/lib/apt/lists/*

# Copy installed Python packages from builder
COPY --from=builder --chown=aegora:aegora /root/.local /home/aegora/.local
COPY --chown=aegora:aegora backend/ /app/backend/

# Environmental security hardening
ENV PATH=/home/aegora/.local/bin:$PATH \
    PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    AEGORA_DEBUG_MODE=false \
    PORT=8000

# Drop to non-privileged user
USER 10001:10001

# Healthcheck for orchestration readiness
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:${PORT}/healthz || exit 1

EXPOSE 8000

# Launch Uvicorn with production concurrency and hardened worker configuration
CMD ["uvicorn", "backend.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "4", "--proxy-headers", "--no-server-header"]
