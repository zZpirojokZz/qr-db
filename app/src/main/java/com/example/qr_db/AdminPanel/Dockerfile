FROM golang:1.21-alpine AS builder
WORKDIR /app
COPY . .
RUN go mod init admin-panel && go build -o admin-panel main.go

FROM alpine:latest
WORKDIR /root/
COPY --from=builder /app/admin-panel .
EXPOSE 8080
CMD ["./admin-panel"]