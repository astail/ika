# ika

[![CircleCI](https://circleci.com/gh/astail/ika.svg?style=svg)](https://circleci.com/gh/astail/ika)

local run

`sbt run`

deploy

```
sbt clean dist
cd ansible
ansible-playbook -i inventory/hosts site.yml
```
