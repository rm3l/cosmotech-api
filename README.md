# cosmotech-api

> Cosmo Tech Cloud Platform API, Organization API, User API

## Building

```shell
./gradlew spotlessApply build --info
```

## Running

### Locally

```shell
java -jar api/build/libs/cosmotech-api-latest.jar
```

### Kubernetes

#### Azure Kubernetes Service (AKS)

* Login, like so:

```shell
az acr login --name csmphoenix
```

* Build and push the container image, e.g.:

```shell
./gradlew :cosmotech-api:jib \
  -Djib.to.image=csmphoenix.azurecr.io/cosmotech-api:latest
```

* Configure the cluster

This assumes you already retrieved the AKS cluster credentials, and configured your
current `kubectl` context accordingly.

Otherwise, run `az aks get-credentials`, e.g.:

```shell
az aks get-credentials \
  --resource-group phoenix \
  --name phoenixAKS
```

* Install the Helm Chart

This uses [Helm](https://helm.sh/); so make sure you have it installed.

```shell
export API_VERSION=latest
helm upgrade --install cosmotech-api-${API_VERSION} \
  api/kubernetes/helm-chart \
  --namespace phoenix \
  --set image.repository=csmphoenix.azurecr.io/cosmotech-api \
  --set image.tag=latest
```

#### Local Kubernetes Cluster

* Spawn a local cluster. Skip if you already have configured a local cluster.
Otherwise, you may want to leverage the [scripts/kubernetes/create-local-k8s-cluster.sh](scripts/kubernetes/create-local-k8s-cluster.sh) script to 
provision a local [Kind](https://kind.sigs.k8s.io/) cluster, along with a private local container 
registry.
  
* Build and push the container image, e.g.:

```shell
./gradlew :cosmotech-api:jib \
  -Djib.allowInsecureRegistries=true \
  -Djib.to.image=localhost:5000/cosmotech-api:latest
```

* Install the Helm Chart

This uses [Helm](https://helm.sh/); so make sure you have it installed.

```shell
export API_VERSION=latest
helm upgrade --install cosmotech-api-${API_VERSION} \
  api/kubernetes/helm-chart \
  --namespace phoenix \
  --set image.repository=localhost:5000/cosmotech-api \
  --set image.tag=latest \
  --set image.pullPolicy=Always
```

## License

    Copyright 2021 Cosmo Tech
    
    Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
    and associated documentation files (the "Software"), to deal in the Software without 
    restriction, including without limitation the rights to use, copy, modify, merge, publish, 
    distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
    Software is furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all copies or 
    substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
    BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
    DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
