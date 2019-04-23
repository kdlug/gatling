# Gatling

## Run test app

```
git clone https://github.com/james-willett/VideoGameDB
```

## Run tests in docker

```console
docker run --network host -it -v $(pwd)/results:/opt/gatling/results -v $(pwd)/user-files:/opt/gatling/user-files  -e JAVA_OPTS="-DUSERS=10 -DRAMPDURATION=5 -DDATION=600" denvazh/gatling
```