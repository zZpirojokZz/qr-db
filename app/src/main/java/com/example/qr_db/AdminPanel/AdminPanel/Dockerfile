FROM golang:1.21-alpine AS builder
WORKDIR /app

# Копируем всё содержимое (включая main.go)
COPY . .

# Инициализируем модуль Go прямо внутри контейнера перед сборкой
RUN go mod init admin-panel && go build -o admin-panel main.go

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/admin-panel .

EXPOSE 8080
CMD ["./admin-panel"]