## Camadas (controller, service, repository)

Por ser um sistema MVP, a arquitura é bem simples, é basicamente é uma arquitetura de camadas, com as seguintes camadas:

- Controller: Responsável por receber as requisições do usuário, e chamar o service correspondente.
- Service: Responsável por implementar a lógica do negócio.
- Infrastrucutre: 
    - dataprovider: Responsável por implementar as operações de acesso a dados.
    - repository: Responsável por implementar as operações de persistência de dados.
- Model: Responsável por representar as entidades do domínio.

## Padrões adotados

Por ser um MVP estmos usando models como entity e em todas as camadas. Com exceção da camada de controller, onde usamos DTO e um mapper para converter entre model e DTO.

## Como os erros são tratados

Por enquanto não temos um mecanismo de tratamento de erros, apenas retornamos o erro para o usuário.

## Como funciona autenticação (JWT, Session, OAuth, etc)

- Vamos por enquanto utilizar a autencicação do Supabase.