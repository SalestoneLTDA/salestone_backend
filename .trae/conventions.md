## Nomes de classes

CamelCase para nomes de classe.

### Exemplo

```java
public class UserController {
    // ...
}
```

## Padrões de pacotes

Todo pacote em minúsculo.

### Exemplo

```java
package com.salestone.backend.user;
```

## Padrões de endpoints

Padrão RESTful para endpoints.

### Exemplo

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ...
}
```

## Estilo de código

- Métodos com CamelCase onde a primeira letra é minúscula.

### Exemplo

```java
public class UserService {

    public void createUser(User user) {
        // ...
    }
}
```

- Variáveis com CamelCase, onde a primeira letra é minúscula.

- Constantes em maiúsculo, separadas por underscore.

- DTOs terem DTO no final do nome, Model também, mapper, controller, assim por diante.

