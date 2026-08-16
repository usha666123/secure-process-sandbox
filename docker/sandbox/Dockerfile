FROM ubuntu:24.04

# 1. Stay as root temporarily to install packages
USER root

# 2. Prevent interactive prompts during installation
ENV DEBIAN_FRONTEND=noninteractive

# 3. Update package lists and install python3 minimal
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    && rm -rf /var/lib/apt/lists/*

# 4. Create the non-privileged system user
RUN useradd -m -s /bin/bash sandbox

# 5. Lock down the security context back to the low-privileged user
USER sandbox
WORKDIR /home/sandbox

CMD ["/bin/bash"]
