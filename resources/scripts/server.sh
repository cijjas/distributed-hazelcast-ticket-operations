#!/bin/bash

SERVER_JARS="../../server/target/tpe2-g7-server-2024.1Q/lib/jars/*"
TARGET_SERVER="ar.edu.itba.pod.tpe2.server.Server"
TARGET_DIR="../../server/target/tpe2-g7-server-2024.1Q"

if [ ! -d "$TARGET_DIR" ]; then
    tar -xzf "../../server/target/tpe2-g7-server-2024.1Q-bin.tar.gz" -C "../../server/target/"
fi



java  --add-opens java.management/sun.management=ALL-UNNAMED \
      --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED \
      -Xmx8g \
      -XX:MaxGCPauseMillis=200 \
      -cp "$SERVER_JARS" "$TARGET_SERVER" "$@"