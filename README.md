# casbin-java-cli

casbin-java-cli is a command-line tool based on jcasbin, enabling you to use all of jcasbin's APIs in the shell.

## Installation

1. Clone project from repository

``` shell
git clone https://github.com/jcasbin/casbin-java-cli.git
```

2. Build project, the jar package will be generated in the `target` directory

``` shell
cd casbin-java-cli
mvn clean install
```

## Options
| options               | description                                  | must | remark                                                    |
|-----------------------|----------------------------------------------|------|-----------------------------------------------------------|
| `-m, --model`         | The path of the model file or model text     | y    | Please wrap it with `""` and separate each line with `\|` |
| `-p, --policy`        | The path of the policy file or policy text   | y    | Please wrap it with `""` and separate each line with `\|` |          
| `-e, --enforce`       | Check permissions                            | n    | Please wrap it with `""`                                  |
| `-ex, --enforceEx`    | Check permissions and get which policy it is | n    | Please wrap it with `""`                                  |
| `-ap, --addPolicy`    | Add a policy rule to the policy file         | n    | Please wrap it with `""`                                  |
| `-rp, --removePolicy` | Remove a policy rule from the policy file    | n    | Please wrap it with `""`                                  |

## Get started

- Check whether Alice has read permission on data1

    ```shell
    java -jar target/casbin-java-cli.jar -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" -e "alice, data1, read"
    ```
    > Allow
    ```shell
    java -jar target/casbin-java-cli.jar -m "[request_definition]|r = sub, obj, act|[policy_definition]|p = sub, obj, act|[role_definition]|g = _, _|[policy_effect]|e = some(where (p.eft == allow))|[matchers]|m = g(r.sub, p.sub) && r.obj == p.obj && r.act == p.act" -p "p, alice, data1, read|p, bob, data2, write|p, data2_admin, data2, read|p, data2_admin, data2, write|g, alice, data2_admin" -e "alice, data1, read"
    ```
  > Allow

- Check whether Alice has write permission for data2. If so, display the effective policy.

    ```shell
    java -jar target/casbin-java-cli.jar -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" -ex "alice, data2, write"
    ```
    > true Reason: [alice, data2, write]

- Add a policy to the policy file

    ```shell
    java -jar target/casbin-java-cli.jar -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" -ap "alice, data2, write"
    ```
    > Add Success

- Delete a policy from the policy file

    ```shell
    java -jar target/casbin-java-cli.jar -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" -rp "alice,data1,read"
    ```
    > Remove Success

