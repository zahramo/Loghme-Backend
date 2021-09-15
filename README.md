# Loghme-Backend


### This repository contains backend part of a website that used to order food. 

## Implementation:
- Java
- Spring
- gitlab-ci-cd
- kubernetes

## You Can Deploy this Project Using Kubernetes :
### `Deploy java backend`
 - Go to kubernetes folder <br />
 - Use this command : kubectl apply -f loghme-back-deployment.yml <br /> 
### `Delete java backend deployment`
 - Use this command :kubectl delete service,deployment loghme-back-end
### `Deploy mysql`
 - Go to kubernetes folder <br />
 - Use this command :kubectl apply -f loghme-db-pv.yml  <br /> 
 - Use this command :kubectl apply -f loghme-db.yml  <br /> 
### `Test mysql deployment`
 - Use this command :kubectl get pods  <br /> 
 - Use this command :kubectl exec -it [loghme-db pod] -- /bin/bash  <br />
### `Delete mysql deployment`
 - Use this command :kubectl delete deployment,svc loghme-db <br />
 - Use this command :kubectl delete pvc mysql-pv-claim <br />
