# Documentação da API - AutoFacil

Bem-vindo à documentação da API AutoFacil. Esta API RESTful foi projetada para gerenciar um sistema de revenda de veículos, permitindo a gestão de usuários, anúncios de veículos, vendas e propostas de compra.

## Primeiros Passos

### URL Base

A URL base para acessar a API deve ser configurada no ambiente. Todos os caminhos de endpoint listados abaixo são relativos a essa URL base.

**Exemplo de URL Base:** `https://api.autofacil.com.br/v1`

### Autenticação

A maioria dos endpoints de consulta (`GET`) é pública.

Operações que criam ou modificam dados (`POST`, `PUT`, `PATCH`, `DELETE`) e que precisam identificar um usuário específico (como um vendedor ou comprador) exigem que as credenciais do usuário (`email` e `senha`) sejam enviadas diretamente no corpo da requisição. Consulte a documentação de cada endpoint para ver os detalhes.

-----

## Recursos da API

A API é organizada em torno dos seguintes recursos principais:

1.  [**Usuários (`/users`)**](#1-recurso-usurários)
2.  [**Veículos (`/vehicles`)**](#2-recurso-veículos)
3.  [**Vendas (`/sales`)**](#3-recurso-vendas)
4.  [**Solicitações de Compra (`/purchase-requests`)**](#4-recurso-solicitações-de-compra)

-----

## 1\. Recurso: Usuários

Endpoints para o gerenciamento de usuários (compradores e vendedores).

### 1.1. Criar Usuário

Cria um novo usuário no sistema.

`POST /users`

**Corpo da Requisição (`application/json`)**

| Campo | Tipo | Descrição | Obrigatório |
| :--- | :--- | :--- | :--- |
| `name` | String | Nome completo do usuário. | Sim |
| `email` | String | Email único do usuário. | Sim |
| `password` | String | Senha do usuário. | Sim |
| `role` | String | Papel do usuário. Valores: `BUYER`, `VENDOR`. | Sim |
| `phonenumber` | String | Número de telefone com DDD. | Sim |
| `cpf` | String | CPF do usuário. | Sim |
| `dateOfBirth` | String | Data de nascimento no formato `AAAA-MM-DD`. | Sim |

**Exemplo de Requisição:**

```json
{
  "name": "Carlos Souza",
  "email": "carlos.souza@example.com",
  "password": "umaSenhaForte123",
  "role": "VENDOR",
  "phonenumber": "81912345678",
  "cpf": "123.456.789-00",
  "dateOfBirth": "1985-08-20"
}
```

**Resposta de Sucesso (200 OK)**

```json
{
  "id": 1,
  "name": "Carlos Souza",
  "email": "carlos.souza@example.com",
  "role": "VENDOR"
}
```

### 1.2. Listar Todos os Usuários

Retorna uma lista de todos os usuários cadastrados.

`GET /users`

**Resposta de Sucesso (200 OK)**

```json
[
  {
    "id": 1,
    "name": "Carlos Souza",
    "email": "carlos.souza@example.com",
    "role": "VENDOR"
  },
  {
    "id": 2,
    "name": "Ana Pereira",
    "email": "ana.pereira@example.com",
    "role": "BUYER"
  }
]
```

### 1.3. Buscar Usuário por ID

Busca e retorna um usuário específico pelo seu ID.

`GET /users/{id}`

**Parâmetros de Rota**

| Parâmetro | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | Long | ID único do usuário. |

**Resposta de Sucesso (200 OK)**

```json
{
  "id": 1,
  "name": "Carlos Souza",
  "email": "carlos.souza@example.com",
  "role": "VENDOR"
}
```

### 1.4. Atualizar Usuário

Atualiza os dados de um usuário existente.

`PUT /users/{id}`

**Corpo da Requisição (`application/json`)**

| Campo | Tipo | Descrição | Obrigatório |
| :--- | :--- | :--- | :--- |
| `name` | String | Novo nome do usuário. | Sim |
| `email`| String | Novo email do usuário. | Sim |
| `phonenumber`| String | Novo telefone do usuário. | Sim |
| `cpf` | String | CPF do usuário (para validação). | Sim |

**Exemplo de Requisição:**

```json
{
  "name": "Carlos Alberto Souza",
  "email": "carlos.souza@newemail.com",
  "phonenumber": "81988776655",
  "cpf": "123.456.789-00"
}
```

**Resposta de Sucesso (200 OK)**

```json
{
  "id": 1,
  "name": "Carlos Alberto Souza",
  "email": "carlos.souza@newemail.com",
  "role": "VENDOR"
}
```

### 1.5. Deletar Usuário

Remove um usuário do sistema.

`DELETE /users/{id}`

**Parâmetros de Rota**

| Parâmetro | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | Long | ID do usuário a ser deletado. |

**Resposta de Sucesso (204 No Content)**

* A resposta não possui corpo.

-----

## 2\. Recurso: Veículos

Endpoints para consultar e gerenciar anúncios de veículos.

### 2.1. Cadastrar Novo Veículo

Cadastra um novo veículo, associando-o a um vendedor autenticado.

`POST /vehicles`

**Corpo da Requisição (`application/json`)**

| Campo | Tipo | Descrição | Obrigatório |
| :--- | :--- | :--- | :--- |
| `brand` | String | Marca do veículo. | Sim |
| `model` | String | Modelo do veículo. | Sim |
| `year` | Integer | Ano de fabricação. | Sim |
| `color` | String | Cor do veículo. | Sim |
| `price` | BigDecimal | Preço de venda. | Sim |
| `vehicleType`| String | Tipo do veículo (ex: "Sedan", "SUV"). | Sim |
| `photoUrls` | Array\<String\> | Lista de URLs das fotos do veículo. | Sim |
| `vendorEmail`| String | Email do vendedor para autenticação. | Sim |
| `vendorPassword` | String | Senha do vendedor para autenticação. | Sim |

**Exemplo de Requisição:**

```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2023,
  "color": "Prata",
  "price": 145000.00,
  "vehicleType": "Sedan",
  "photoUrls": [
    "https://example.com/foto1.jpg",
    "https://example.com/foto2.jpg"
  ],
  "vendorEmail": "carlos.souza@example.com",
  "vendorPassword": "umaSenhaForte123"
}
```

**Resposta de Sucesso (200 OK)**

```json
{
    "id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2023,
    "color": "Prata",
    "price": 145000.00,
    "vehicleType": "Sedan",
    "sold": false,
    "photoUrls": [
        "https://example.com/foto1.jpg",
        "https://example.com/foto2.jpg"
    ],
    "vendorId": 1,
    "vendorName": "Carlos Souza"
}
```

### 2.2. Listar Veículos (com Filtros e Paginação)

Retorna uma lista paginada de veículos.

`GET /vehicles`

**Parâmetros de Consulta (Query)**

| Parâmetro | Tipo | Descrição | Padrão |
| :--- | :--- | :--- | :--- |
| `brand` | String | Filtra pela marca. (Opcional) | |
| `model` | String | Filtra pelo modelo. (Opcional) | |
| `year` | Integer| Filtra pelo ano. (Opcional) | |
| `page` | Integer| Número da página. | `0` |
| `size` | Integer| Resultados por página. | `10` |

**Resposta de Sucesso (200 OK)**

```json
{
  "content": [
    {
      "id": 1,
      "brand": "Toyota",
      "model": "Corolla",
      "year": 2023,
      "color": "Prata",
      "price": 145000.00,
      "vehicleType": "Sedan",
      "sold": false,
      "photoUrls": ["https://example.com/foto1.jpg"],
      "vendorId": 1,
      "vendorName": "Carlos Souza"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10, "...": "..." },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

### 2.3. Buscar Veículo por ID

Busca e retorna um veículo específico pelo seu ID.

`GET /vehicles/{id}`

**Resposta de Sucesso (200 OK)**

```json
{
    "id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2023,
    "color": "Prata",
    "price": 145000.00,
    "vehicleType": "Sedan",
    "sold": false,
    "photoUrls": [
        "https://example.com/foto1.jpg"
    ],
    "vendorId": 1,
    "vendorName": "Carlos Souza"
}
```

### 2.4. Atualizar Veículo

Atualiza os dados de um anúncio de veículo.

`PUT /vehicles/{id}`

**Exemplo de Requisição:**

```json
{
  "brand": "Toyota",
  "model": "Corolla XEi",
  "year": 2023,
  "color": "Prata",
  "price": 142500.00,
  "vehicleType": "Sedan",
  "photoUrls": [
    "https://example.com/foto1.jpg",
    "https://example.com/foto_nova.jpg"
  ]
}
```

**Resposta de Sucesso (200 OK)**

* Retorna o objeto completo do veículo atualizado.

### 2.5. Marcar Veículo como Vendido

Atualiza o status de um veículo para "vendido".

`PATCH /vehicles/{id}/sold`

**Resposta de Sucesso (200 OK)**

* Retorna o objeto do veículo atualizado, com o campo `"sold": true`.

### 2.6. Deletar Veículo

Remove um anúncio de veículo do sistema.

`DELETE /vehicles/{id}`

**Resposta de Sucesso (204 No Content)**

* A resposta não possui corpo.

-----

## 3\. Recurso: Vendas

Endpoints para o gerenciamento de registros de vendas.

### 3.1. Registrar Venda

Registra a venda de um veículo, associando vendedor e comprador.

`POST /sales`

**Exemplo de Requisição:**

```json
{
    "vehicleId": 1,
    "buyerId": 2,
    "price": 142000.00,
    "vendorEmail": "carlos.souza@example.com",
    "vendorPassword": "umaSenhaForte123"
}
```

**Resposta de Sucesso (201 Created)**

```json
{
    "id": 1,
    "vehicleId": 1,
    "buyerId": 2,
    "vendorId": 1,
    "price": 142000.00,
    "saleDate": "2025-06-11T15:30:00"
}
```

### 3.2. Listar Todas as Vendas

Retorna uma lista com todos os registros de vendas.

`GET /sales`

**Resposta de Sucesso (200 OK)**

* Retorna um array de objetos de Venda.

### 3.3. Buscar Venda por ID

Busca e retorna um registro de venda pelo seu ID.

`GET /sales/{id}`

**Resposta de Sucesso (200 OK)**

* Retorna um único objeto de Venda.

-----

## 4. Recurso: Solicitações de Compra

Endpoints para o gerenciamento de propostas de compra.

### 4.1. Criar Solicitação de Compra

Permite que um comprador autenticado crie uma solicitação de compra para um veículo.

`POST /purchase-requests`

**Corpo da Requisição (`application/json`)**

| Campo | Tipo | Descrição | Obrigatório |
| :--- | :--- | :--- | :--- |
| `vehicleId` | Long | ID do veículo desejado. | Sim |
| `buyerEmail` | String | Email do comprador para autenticação. | Sim |
| `buyerPassword` | String | Senha do comprador para autenticação. | Sim |

**Exemplo de Requisição:**
```json
{
  "vehicleId": 1,
  "buyerEmail": "comprador@example.com",
  "buyerPassword": "senhaDoComprador"
}
```

**Resposta de Sucesso (201 Created)**

```json
{
    "id": 1,
    "vehicleId": 1,
    "buyerId": 2,
    "vendorId": 1,
    "requestDate": "2025-06-11T15:02:25.123456",
    "status": "PENDING",
    "responseDate": null
}
```

### 4.2. Listar Solicitações por Comprador (Seguro)

Retorna todas as solicitações de compra que um usuário `BUYER` fez. As credenciais são enviadas no corpo da requisição por segurança.

`POST /purchase-requests/query/by-buyer`

**Corpo da Requisição (`application/json`)**

| Parâmetro | Tipo | Descrição | Obrigatório |
| :--- | :--- | :--- | :--- |
| `email` | String | Email do comprador para autenticação. | Sim |
| `password`| String | Senha do comprador para autenticação. | Sim |

**Exemplo de Requisição:**
```json
{
  "email": "comprador@example.com",
  "password": "senhaDoComprador"
}
```

**Resposta de Sucesso (200 OK)**
* Retorna um array de objetos de Solicitação de Compra.

### 4.3. Listar Solicitações por Vendedor (Seguro)

Retorna todas as solicitações de compra que um usuário `VENDOR` recebeu. As credenciais são enviadas no corpo da requisição por segurança.

`POST /purchase-requests/query/by-vendor`

**Corpo da Requisição (`application/json`)**

| Parâmetro | Tipo | Descrição | Obrigatório |
| :--- | :--- | :--- | :--- |
| `email` | String | Email do vendedor para autenticação. | Sim |
| `password`| String | Senha do vendedor para autenticação. | Sim |

**Exemplo de Requisição:**
```json
{
  "email": "vendedor@example.com",
  "password": "senhaDoVendedor"
}
```

**Resposta de Sucesso (200 OK)**
* Retorna um array de objetos de Solicitação de Compra.


### 4.4. Aceitar Solicitação de Compra

Permite que um usuário `VENDOR` aceite uma solicitação de compra pendente.

`PUT /purchase-requests/{id}/accept`

**Corpo da Requisição (`application/json`)**
```json
{
  "vendorEmail": "vendedor@example.com",
  "vendorPassword": "senhaDoVendedor"
}
```

**Resposta de Sucesso (200 OK)**
* Retorna o objeto da Solicitação de Compra atualizado com status `ACCEPTED`.

### 4.5. Negar Solicitação de Compra

Permite que um usuário `VENDOR` negue uma solicitação de compra pendente.

`PUT /purchase-requests/{id}/deny`

**Corpo da Requisição (`application/json`)**
```json
{
  "vendorEmail": "vendedor@example.com",
  "vendorPassword": "senhaDoVendedor"
}
```

**Resposta de Sucesso (200 OK)**
* Retorna o objeto da Solicitação de Compra atualizado com status `DENIED`.
