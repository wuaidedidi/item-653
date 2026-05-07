#!/usr/bin/env bash
set -euo pipefail

export TZ="${TZ:-Asia/Shanghai}"
export DB_HOST="${DB_HOST:-127.0.0.1}"
export DB_PORT="${DB_PORT:-3306}"
export DB_NAME="${DB_NAME:-library}"
export DB_USERNAME="${DB_USERNAME:-root}"
export DB_PASSWORD="${DB_PASSWORD:-root}"
export JWT_SECRET="${JWT_SECRET:-LibraryManagementSystem653SecretKeyForJWTTokenGeneration2026}"

mkdir -p /run/mysqld /var/lib/mysql /var/log/mysql
chown -R mysql:mysql /run/mysqld /var/lib/mysql /var/log/mysql

if [ ! -d /var/lib/mysql/mysql ]; then
  echo "Initializing MySQL data directory..."
  mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql
fi

echo "Starting MySQL on port 3306..."
mysqld --user=mysql --datadir=/var/lib/mysql --bind-address=127.0.0.1 &
MYSQL_PID=$!

until mysqladmin ping --protocol=socket -uroot --silent; do
  sleep 2
done

mysql --protocol=socket -uroot <<SQL
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '${DB_PASSWORD}';
CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;
CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
FLUSH PRIVILEGES;
SQL

if [ -f /app/db/init.sql ]; then
  mysql -h127.0.0.1 -uroot -p"${DB_PASSWORD}" "${DB_NAME}" < /app/db/init.sql
fi

echo "Starting Spring Boot backend on port 8080..."
java -Dfile.encoding=UTF-8 -Duser.timezone="${TZ}" -jar /app/backend/target/*.jar &
BACKEND_PID=$!

until nc -z 127.0.0.1 8080; do
  sleep 2
done

echo "Starting Nginx frontend on port 80..."
nginx

echo "Library management system is ready."
echo "Frontend: http://localhost:80"
echo "Backend API: http://localhost:8080/api"
echo "MySQL: 127.0.0.1:3306"

trap 'nginx -s quit || true; kill "$BACKEND_PID" "$MYSQL_PID" 2>/dev/null || true' TERM INT
wait "$BACKEND_PID"
