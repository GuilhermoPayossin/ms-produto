# ms-produto — Microserviço de Produtos

API REST desenvolvida com **Spring Boot** para gerenciamento de produtos e categorias. Este documento explica detalhadamente a arquitetura do projeto, o propósito de cada pasta, o que pertence a cada camada e como expandir a API com novos recursos.

---

## Sumário

1. [Stack Tecnológica](#stack-tecnológica)
2. [Como Executar](#como-executar)
3. [Estrutura de Pastas](#estrutura-de-pastas)
4. [Arquitetura em Camadas](#arquitetura-em-camadas)
5. [Modelo de Dados](#modelo-de-dados)
6. [Endpoints da API](#endpoints-da-api)
7. [Tratamento de Erros](#tratamento-de-erros)
8. [Como Expandir a API](#como-expandir-a-api)

---

## Stack Tecnológica

| Tecnologia                   | Versão  | Papel                                          |
|------------------------------|---------|------------------------------------------------|
| Java                         | 17      | Linguagem principal                            |
| Spring Boot                  | 4.0.2   | Framework principal (web + DI + auto-config)   |
| Spring Data JPA / Hibernate  | -       | ORM — mapeia classes Java para tabelas SQL     |
| H2 Database                  | -       | Banco de dados em memória (perfil `test`)      |
| Lombok                       | -       | Reduz boilerplate (getters, construtores, etc) |
| Bean Validation (Jakarta)    | -       | Validação automática dos campos dos DTOs       |
| Maven                        | -       | Gerenciamento de dependências e build          |

---

## Como Executar

```bash
# Na raiz do projeto
./mvnw spring-boot:run
```

A aplicação sobe na porta **8080** com o perfil `test` ativado automaticamente.

- **API base:** `http://localhost:8080`
- **Console H2 (banco em memória):** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Usuário: `sa` / Senha: _(em branco)_

---

## Estrutura de Pastas

```
src/
└── main/
│   ├── java/com/guilhermopayossin/github/ms_produto/
│   │   ├── MsProdutoApplication.java   ← Ponto de entrada da aplicação
│   │   │
│   │   ├── controller/                 ← Camada HTTP (recebe requisições REST)
│   │   │   ├── ProdutoController.java
│   │   │   └── CategoriaController.java
│   │   │
│   │   ├── services/                   ← Camada de negócio (lógica da aplicação)
│   │   │   ├── ProdutoService.java
│   │   │   └── CategoriaService.java
│   │   │
│   │   ├── repository/                 ← Camada de acesso a dados (JPA)
│   │   │   ├── ProdutoRepository.java
│   │   │   └── CategoriaRepository.java
│   │   │
│   │   ├── enities/                    ← Entidades JPA (mapeadas para tabelas do banco)
│   │   │   ├── Produto.java
│   │   │   └── Categoria.java
│   │   │
│   │   ├── dto/                        ← Objetos de transferência de dados
│   │   │   ├── ProdutoDTO.java
│   │   │   └── CategoriaDTO.java
│   │   │
│   │   └── exeptions/                  ← Tratamento centralizado de erros
│   │       ├── ResourceNotFoundException.java
│   │       ├── DatabaseException.java
│   │       ├── dto/
│   │       │   ├── CustomErrorDTO.java
│   │       │   ├── ValidationErrorDTO.java
│   │       │   └── FieldMessageDTO.java
│   │       └── handler/
│   │           └── GlobalExceptionHandler.java
│   │
│   └── resources/
│       ├── application.properties        ← Configurações gerais
│       ├── application-test.properties   ← Configurações do perfil "test" (H2)
│       └── import.sql                    ← Dados iniciais carregados na inicialização
│
└── test/
    └── java/...
        └── MsProdutoApplicationTests.java
```

---

## Arquitetura em Camadas

Esta API segue o padrão **N-Tier (Arquitetura em Camadas)**, onde cada camada tem uma única responsabilidade e se comunica apenas com a camada adjacente:

```
┌─────────────────────────────────────────────┐
│              Cliente (HTTP)                  │
│          (Postman, front-end, etc.)          │
└──────────────────┬──────────────────────────┘
                   │  JSON
                   ▼
┌─────────────────────────────────────────────┐
│             CONTROLLER  (camada Web)         │
│  @RestController  @RequestMapping            │
│  • Recebe requisições HTTP                   │
│  • Valida a entrada com @Valid               │
│  • Delega para o Service                     │
│  • Retorna ResponseEntity com o HTTP status  │
└──────────────────┬──────────────────────────┘
                   │  DTO
                   ▼
┌─────────────────────────────────────────────┐
│              SERVICE  (camada de negócio)    │
│  @Service  @Transactional                    │
│  • Contém toda a lógica de negócio           │
│  • Converte Entity ↔ DTO                     │
│  • Lança exceções de domínio                 │
│  • Gerencia transações (@Transactional)      │
└──────────────────┬──────────────────────────┘
                   │  Entity
                   ▼
┌─────────────────────────────────────────────┐
│           REPOSITORY  (camada de dados)      │
│  JpaRepository<Entity, ID>                   │
│  • Interface gerenciada pelo Spring Data     │
│  • Fornece CRUD automático (save, findById…) │
│  • Queries personalizadas com @Query         │
└──────────────────┬──────────────────────────┘
                   │  SQL
                   ▼
┌─────────────────────────────────────────────┐
│                  BANCO DE DADOS              │
│         H2 in-memory (perfil test)           │
│         tb_produto / tb_categoria            │
└─────────────────────────────────────────────┘
```

### Detalhamento de cada pacote

#### `controller/`
**O que é:** A porta de entrada da aplicação. Cada classe aqui é um **Controller REST** anotado com `@RestController`.

**O que vai aqui:**
- Uma classe por recurso da API (ex.: `ProdutoController`, `CategoriaController`)
- Métodos mapeados com `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Retornam sempre `ResponseEntity<T>` com o status HTTP correto
- Não contêm nenhuma lógica de negócio — apenas delegam para o `Service`

**Exemplo de fluxo:** `GET /produtos/1` → `ProdutoController.getProdutoById(1)` → chama `ProdutoService.findProdutoById(1)` → retorna `ResponseEntity.ok(produtoDTO)`

---

#### `services/`
**O que é:** O coração da aplicação. Toda a **lógica de negócio** fica aqui.

**O que vai aqui:**
- Uma classe por recurso, anotada com `@Service`
- Métodos anotados com `@Transactional` (garante atomicidade)
- Conversão entre `Entity` e `DTO` (ex.: `new ProdutoDTO(produto)`)
- Validações de negócio e lançamento de exceções customizadas
- Nunca retorna `Entity` diretamente para o Controller — sempre retorna `DTO`

---

#### `repository/`
**O que é:** A camada de acesso ao banco de dados, 100% gerenciada pelo **Spring Data JPA**.

**O que vai aqui:**
- Uma interface por entidade que estende `JpaRepository<Entidade, TipoDoId>`
- Métodos CRUD já disponíveis automaticamente: `findAll()`, `findById()`, `save()`, `deleteById()`, etc.
- Queries personalizadas com `@Query("SELECT ...")` ou por convenção de nome (ex.: `findByNome(String nome)`)

---

#### `enities/` _(nota: o nome do pacote contém um typo — a grafia correta seria `entities`)_
**O que é:** As classes que representam as **tabelas do banco de dados** (mapeamento objeto-relacional com JPA).

**O que vai aqui:**
- Uma classe por tabela, anotada com `@Entity` e `@Table(name = "tb_...")`
- Campos mapeados com `@Column`, `@Id`, `@GeneratedValue`
- Relacionamentos com `@ManyToOne`, `@OneToMany`, `@ManyToMany`
- Anotações do Lombok (`@Getter`, `@Setter`, `@AllArgsConstructor`, etc.) para evitar boilerplate

---

#### `dto/`
**O que é:** **Data Transfer Objects** — classes que definem exatamente quais dados trafegam entre o cliente e a API.

**Por que usar DTO em vez de Entity diretamente?**
- Evita expor campos internos/sensíveis da Entity
- Permite validações com Bean Validation (`@NotBlank`, `@Positive`, etc.)
- Desacopla a estrutura do banco da estrutura da API

**O que vai aqui:**
- Uma classe por recurso (ex.: `ProdutoDTO`, `CategoriaDTO`)
- Anotações de validação nos campos (`@NotBlank`, `@NotNull`, `@Size`, `@Positive`)
- Construtor que aceita a Entity correspondente (para conversão Entity → DTO)

---

#### `exeptions/` _(nota: o nome do pacote contém um typo — a grafia correta seria `exceptions`)_
**O que é:** Tratamento **centralizado** de erros. Em vez de cada método tratar seus próprios erros, um único ponto captura tudo.

**Subpastas:**

- **`exeptions/` (raiz):** Classes de exceção customizadas do domínio:
  - `ResourceNotFoundException` — lançada quando um ID não é encontrado (→ HTTP 404)
  - `DatabaseException` — lançada em violações de integridade (→ HTTP 409)

- **`exeptions/dto/`:** DTOs do corpo de resposta de erro:
  - `CustomErrorDTO` — resposta padrão de erro (`timestamp`, `status`, `error`, `path`)
  - `ValidationErrorDTO` — estende `CustomErrorDTO` com lista de campos inválidos
  - `FieldMessageDTO` — representa um campo inválido (`fieldName`, `message`)

- **`exeptions/handler/`:** O interceptador global:
  - `GlobalExceptionHandler` — anotado com `@RestControllerAdvice`, captura exceções de **todos os controllers** e retorna respostas JSON padronizadas

---

#### `resources/`
**O que é:** Arquivos de configuração e dados iniciais.

| Arquivo                          | Propósito                                                        |
|----------------------------------|------------------------------------------------------------------|
| `application.properties`         | Configuração principal: ativa o perfil `test`, desativa open-in-view |
| `application-test.properties`    | Configurações do H2, console web, JPA/Hibernate                  |
| `import.sql`                     | Inserções SQL executadas automaticamente na inicialização do H2  |

---

## Modelo de Dados

```
┌──────────────────┐        ┌──────────────────────────┐
│   tb_categoria   │        │       tb_produto          │
├──────────────────┤        ├──────────────────────────┤
│ id   BIGINT (PK) │◄───┐   │ id          BIGINT (PK)  │
│ nome VARCHAR     │    └───│ categoria_id BIGINT (FK) │
└──────────────────┘        │ nome        VARCHAR       │
                            │ descricao   VARCHAR       │
                            │ valor       DOUBLE        │
                            └──────────────────────────┘
```

- Uma **Categoria** pode ter muitos **Produtos** (`@OneToMany`)
- Um **Produto** pertence a exatamente uma **Categoria** (`@ManyToOne`)

---

## Endpoints da API

### Categorias — `/categorias`

| Método   | Endpoint           | Descrição                  | Status de Sucesso |
|----------|--------------------|----------------------------|--------------------|
| `GET`    | `/categorias`      | Lista todas as categorias  | `200 OK`           |
| `GET`    | `/categorias/{id}` | Busca categoria por ID     | `200 OK`           |
| `POST`   | `/categorias`      | Cria nova categoria        | `201 Created`      |
| `PUT`    | `/categorias/{id}` | Atualiza categoria por ID  | `200 OK`           |
| `DELETE` | `/categorias/{id}` | Remove categoria por ID    | `204 No Content`   |

**Corpo da requisição (POST/PUT):**
```json
{
  "nome": "Eletrônicos"
}
```

---

### Produtos — `/produtos`

| Método   | Endpoint         | Descrição              | Status de Sucesso |
|----------|------------------|------------------------|--------------------|
| `GET`    | `/produtos`      | Lista todos os produtos | `200 OK`          |
| `GET`    | `/produtos/{id}` | Busca produto por ID   | `200 OK`           |
| `POST`   | `/produtos`      | Cria novo produto      | `201 Created`      |
| `PUT`    | `/produtos/{id}` | Atualiza produto por ID | `200 OK`          |
| `DELETE` | `/produtos/{id}` | Remove produto por ID  | `204 No Content`   |

**Corpo da requisição (POST/PUT):**
```json
{
  "nome": "Smartphone Galaxy A54",
  "descricao": "Samsung Galaxy A54 5G, 128GB, Preto",
  "valor": 1799.00,
  "categoria": {
    "id": 1
  }
}
```

**Resposta de sucesso:**
```json
{
  "id": 1,
  "nome": "Smartphone Galaxy A54",
  "descricao": "Samsung Galaxy A54 5G, 128GB, Preto",
  "valor": 1799.00,
  "categoria": {
    "id": 1,
    "nome": "Eletrônicos"
  }
}
```

---

## Tratamento de Erros

Todos os erros retornam um JSON padronizado:

```json
{
  "timestamp": "2025-01-01T10:00:00Z",
  "status": 404,
  "error": "Recurso não encontrado: ID - 99",
  "path": "/produtos/99"
}
```

Em erros de validação (HTTP `422 Unprocessable Entity`), o corpo inclui a lista de campos inválidos:

```json
{
  "timestamp": "2025-01-01T10:00:00Z",
  "status": 422,
  "error": "Dados inválidos",
  "path": "/produtos",
  "errors": [
    { "fieldName": "nome", "message": "Campo nome é obrigatório" },
    { "fieldName": "valor", "message": "O campo valor não pode ser menor que 0" }
  ]
}
```

| Exceção                          | HTTP Status                  | Quando ocorre                                      |
|----------------------------------|------------------------------|----------------------------------------------------|
| `ResourceNotFoundException`      | `404 Not Found`              | ID inexistente no banco                            |
| `DatabaseException`              | `409 Conflict`               | Violação de integridade (ex.: categoria inválida)  |
| `MethodArgumentNotValidException`| `422 Unprocessable Entity`   | Validação de campos do DTO falhou                  |
| `HttpMessageNotReadableException`| `400 Bad Request`            | JSON malformado no corpo da requisição             |
| `MethodArgumentTypeMismatchException` | `400 Bad Request`       | Parâmetro de rota com tipo incorreto (ex.: `/produtos/abc`) |
| `Exception` (genérico)           | `500 Internal Server Error`  | Qualquer erro inesperado não tratado               |

---

## Como Expandir a API

Para adicionar um novo recurso à API (ex.: **Fornecedor**), siga este roteiro de **5 passos** replicando o padrão já existente:

### Passo 1 — Criar a Entity (`enities/`)

```java
// src/main/java/.../enities/Fornecedor.java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_fornecedor")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cnpj;
}
```

> A Entity representa a tabela no banco. O JPA criará a tabela `tb_fornecedor` automaticamente.

---

### Passo 2 — Criar o Repository (`repository/`)

```java
// src/main/java/.../repository/FornecedorRepository.java
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    // Queries personalizadas, se necessário:
    // List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}
```

> Apenas estender `JpaRepository` já fornece `findAll`, `findById`, `save`, `deleteById` e muito mais.

---

### Passo 3 — Criar o DTO (`dto/`)

```java
// src/main/java/.../dto/FornecedorDTO.java
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FornecedorDTO {
    private Long id;

    @NotBlank(message = "Campo nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Campo CNPJ é obrigatório")
    private String cnpj;

    public FornecedorDTO(Fornecedor fornecedor) {
        id = fornecedor.getId();
        nome = fornecedor.getNome();
        cnpj = fornecedor.getCnpj();
    }
}
```

> O DTO protege os campos internos da Entity e habilita a validação automática via Bean Validation.

---

### Passo 4 — Criar o Service (`services/`)

```java
// src/main/java/.../services/FornecedorService.java
@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Transactional(readOnly = true)
    public List<FornecedorDTO> findAllFornecedores() {
        return fornecedorRepository.findAll()
                .stream().map(FornecedorDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorDTO findFornecedorById(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado: ID - " + id)
        );
        return new FornecedorDTO(fornecedor);
    }

    @Transactional
    public FornecedorDTO saveFornecedor(FornecedorDTO dto) {
        Fornecedor fornecedor = new Fornecedor();
        copyDtoToEntity(dto, fornecedor);
        fornecedor = fornecedorRepository.save(fornecedor);
        return new FornecedorDTO(fornecedor);
    }

    @Transactional
    public FornecedorDTO updateFornecedor(Long id, FornecedorDTO dto) {
        try {
            Fornecedor fornecedor = fornecedorRepository.getReferenceById(id);
            copyDtoToEntity(dto, fornecedor);
            fornecedor = fornecedorRepository.save(fornecedor);
            return new FornecedorDTO(fornecedor);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado: ID - " + id);
        }
    }

    @Transactional
    public void deleteFornecedorById(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado: ID - " + id);
        }
        fornecedorRepository.deleteById(id);
    }

    private void copyDtoToEntity(FornecedorDTO dto, Fornecedor fornecedor) {
        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());
    }
}
```

---

### Passo 5 — Criar o Controller (`controller/`)

```java
// src/main/java/.../controller/FornecedorController.java
@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping
    public ResponseEntity<List<FornecedorDTO>> getAll() {
        return ResponseEntity.ok(fornecedorService.findAllFornecedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.findFornecedorById(id));
    }

    @PostMapping
    public ResponseEntity<FornecedorDTO> create(@RequestBody @Valid FornecedorDTO dto) {
        dto = fornecedorService.saveFornecedor(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorDTO> update(@PathVariable Long id,
                                                @RequestBody @Valid FornecedorDTO dto) {
        return ResponseEntity.ok(fornecedorService.updateFornecedor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fornecedorService.deleteFornecedorById(id);
        return ResponseEntity.noContent().build();
    }
}
```

> Após esses 5 passos, a API `GET /fornecedores`, `POST /fornecedores`, `PUT /fornecedores/{id}` e `DELETE /fornecedores/{id}` já estará funcional, com validação e tratamento de erros automáticos.

---

### Checklist para cada novo recurso

- [ ] **Entity** em `enities/` com `@Entity`, `@Table`, campos e Lombok
- [ ] **Repository** em `repository/` estendendo `JpaRepository<Entidade, Long>`
- [ ] **DTO** em `dto/` com validações Bean Validation e construtor que aceita a Entity
- [ ] **Service** em `services/` com `@Service`, `@Transactional` e conversões Entity ↔ DTO
- [ ] **Controller** em `controller/` com `@RestController`, `@RequestMapping` e `ResponseEntity`
- [ ] **Dados iniciais** (opcional) em `import.sql` para ambiente de teste
