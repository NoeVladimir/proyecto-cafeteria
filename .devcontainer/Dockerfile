FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu

RUN find /etc/apt -type f \( -name '*.list' -o -name '*.sources' \) \
	-exec sed -i 's|http://azure.archive.ubuntu.com/ubuntu|https://archive.ubuntu.com/ubuntu|g' {} + \
	&& apt-get update \
	&& apt-get install -y --no-install-recommends git maven postgresql-client \
	&& rm -rf /var/lib/apt/lists/*

WORKDIR /workspace