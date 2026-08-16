FROM ubuntu:24.04

# Create a non-privileged system user named sandbox with a home directory
RUN useradd -m -s /bin/bash sandbox

# Switch execution context away from root to the new user
USER sandbox

# Set the active working directory safely inside the user's home space
WORKDIR /home/sandbox

# Default command if no arguments are passed
CMD ["/bin/bash"]
