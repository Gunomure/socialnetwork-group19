Полезные ссылки
https://stackoverflow.com/questions/52444408/curl-7-failed-connect-to-xx-xx-xx-xx80-connection-refused/52527581

Добавить эмулятор платформы amd64:
docker run --privileged --rm tonistiigi/binfmt --install amd64
Добавить эмулятор платформы arm64:
docker run --privileged --rm tonistiigi/binfmt --install arm64
Эмулятор перестает работать после перезапуска ПК/сервераx

Для локального запуска необходимо:
1. postgres.yaml заменить
javaprogroup19/postgres:1.0-ARM
на
javaprogroup19/postgres:1.0
или любую актуальную сборку без указания платформы
2. backend.yaml заменить
javaprogroup19/impl:1.0.3-ARM
на
javaprogroup19/impl:1.0.0-SNAPSHOT
или любую актуальную сборку без указания платформы

Создать namespace
kubectl create namespace group19-prod
kubectl create namespace group19-test

Последовательность запуска деплойментов:
kubectl apply -f postgres-config.yaml
kubectl apply -f postgres-secret.yaml
kubectl apply -f postgres.yaml
kubectl apply -f backend-config.yaml
kubectl apply -f backend-secret.yaml
kubectl apply -f backend.yaml
kubectl apply -f ingress-g19.yaml

Подключить ingress
minikube addons enable ingress

Подключить ingress dns
minikube addons enable ingress-dns

Подключить dashboard
minikube dashboard

Редактировать конфиг dashboard
kubectl edit svc kubernetes-dashboard -n kubernetes-dashboard
В текущем конфиге
ports:
  - nodePort: 30001
    port: 80
    protocol: TCP
    targetPort: 9090
  selector:
    k8s-app: kubernetes-dashboard
  sessionAffinity: None
  type: NodePort

