# kubernetes-dashboard
kubernetes dashboard project for fun.
## backend api command
### crd
#### get crd list
```bash
curl -X GET -H "Content-Type: application/json" localhost:10003/api/kubernetes/custom-resource-definitions
```
### deploy
#### get default deployments
```bash
curl -X POST -H "Content-Type: application/json" localhost:10003/api/kubernetes/namespace/default/deployments
```
#### scale deploy
```bash
curl -X PUT -H "Content-Type: application/json" localhost:10003/api/kubernetes/namespace/default/deployments/scale -d '{"deployName":"test","replicas":2}'
```
## depends on libraries
- [spring boot](https://projects.spring.io/spring-boot/)
- [vue3](https://vuejs.org/)
- [vuetify](https://vuetifyjs.com/en/)
