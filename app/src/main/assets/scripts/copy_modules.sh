#!/system/bin/sh


SOURCE_DIR="/data/data/cn.nightrainmilkyway.tritium/files/modules/"
TARGET_DIR="/storage/emulated/0/Download/"

mkdir -p "$TARGET_DIR"

for file in "$SOURCE_DIR"*; do
    if [ -f "$file" ]; then
        cp "$file" "$TARGET_DIR"
    fi
done

exit 0

