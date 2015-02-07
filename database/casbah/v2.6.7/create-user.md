### 管理者

```javascript
use admin
db.createUser({
  "user": "$username",
  "pwd": "$password",
  "roles": ["dbOwner", "userAdminAnyDatabase"]
})
```

> 他のDBの中身は見れない。
> 見れるようにするにはroleに「readWriteAnyDatabase」とかを追加。


### 各DBのオーナー

```javascript
use $database
db.createUser({
  "user": "$username",
  "pwd": "$password",
  "roles": ["dbOwner"]
})
```

> 「dbOwner」つけとけば自分のDBは自由にできる。


### 接続

```bash
mongo $database -u $username -p $password
```


### その他のrole

http://docs.mongodb.org/manual/reference/built-in-roles/

自分でroleも作れる。


