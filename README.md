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
| `-AF, --addFuntion`   | Add custom funtion                           | n    | Please wrap it with `""` and separate each line with `\|` |
| `-ap, --addPolicy`    | Add a policy rule to the policy file         | n    | Please wrap it with `""`                                  |
| `-rp, --removePolicy` | Remove a policy rule from the policy file    | n    | Please wrap it with `""`                                  |

## Get started

- Check whether Alice has read permission on data1

    ```shell
    ./casbin enforce -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" "alice" "data1" "read"
    ```
    > {"allow":true,"explain":null}
    ```shell
    ./casbin enforce -m "[request_definition]\nr = sub, obj, act\n[policy_definition]\np = sub, obj, act\n[role_definition]\ng = _, _\n[policy_effect]\ne = some(where (p.eft == allow))\n[matchers]\nm = g(r.sub, p.sub) && r.obj == p.obj && r.act == p.act" -p "p, alice, data1, read\np, bob, data2, write\np, data2_admin, data2, read\np, data2_admin, data2, write\ng, alice, data2_admin" "alice" "data1" "read"
    ```
    > {"allow":true,"explain":null}

- Check whether Alice has write permission for data2. If so, display the effective policy.

    ```shell
    ./casbin enforceEx -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" "alice" "data2" "write"
    ```
    > {"allow":true,"explain":["data2_admin","data2","write"]}

- Add a policy to the policy file

    ```shell
    ./casbin addPolicy -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" "alice" "data2" "write"
    ```
    > {"allow":true,"explain":null}

- Delete a policy from the policy file

    ```shell
    ./casbin removePolicy -m "examples/rbac_model.conf" -p "examples/rbac_policy.csv" "alice" "data2" "write"
    ```
    > {"allow":true,"explain":null}

