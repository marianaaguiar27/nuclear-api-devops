# Nuclear no Dia a Dia - API (Java Advanced)

Challenge FIAP 2026 - Parceiro: CTMSP (Centro Tecnológico da Marinha em São Paulo)


## Grupo
| Nome | RM | Turma |
|------|----| ------|
|   Manuella Rinaldi   |  567915  |  2TDSPS  |
|   Gustavo Miguel Martins de Oliveira   |  566666  |  2TDSPB  |
|   Mariana de Paula Aguiar   |  566850  |  2TDSPB  |


## Solução
API RESTful que apoia uma solução educativa sobre tecnologia nuclear. Ela guarda
curiosidades ("conteúdos") sobre onde a tecnologia nuclear aparece no dia a dia,
organizadas por categoria (saúde, industria, agricultura, pesquisa e energia) e por
nível de dificuldade. O app mobile e o quiz consomem esses conteúdos.

## Tecnologias
Java 17, Spring Boot 4.1, Gradle, Spring Data JPA, Spring Validation,
springdoc 3.1 (Swagger/OpenAPI), H2 (padrao) e Oracle (perfil `oracle`).

## Como executar
Requisito: JDK 17 (o Gradle e baixado automaticamente pelo wrapper).

```bash
./gradlew bootRun
```

No Windows:

```powershell
.\gradlew.bat bootRun
```

- Swagger: http://localhost:8080/swagger-ui.html
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:nuclear`, user `sa`, senha vazia)

Os dados de teste são carregados automaticamente (`src/main/resources/data.sql`).

### Rodando com Oracle (opcional)
Defina as variáveis de ambiente (não versione credenciais):

```bash
export ORACLE_URL="jdbc:oracle:thin:@//host:1521/servico"
export ORACLE_USER="usuario"
export ORACLE_PASSWORD="senha"
./gradlew bootRun --args='--spring.profiles.active=oracle'
```

## Endpoints
| Metodo | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/conteudos` | Lista tudo (filtro opcional `?categoria=SAUDE`) |
| GET | `/api/conteudos/busca?texto=radiacao` | Busca por texto (titulo ou descrição) |
| GET | `/api/conteudos/{id}` | Busca por id |
| POST | `/api/conteudos` | Cadastra |
| PUT | `/api/conteudos/{id}` | Atualiza |
| DELETE | `/api/conteudos/{id}` | Remove |

Exemplo de corpo (POST/PUT):

```json
{
  "titulo": "Detector de fumaca",
  "categoria": "INDUSTRIA",
  "descricao": "Alguns detectores de fumaca usam uma pequena fonte radioativa para ionizar o ar e perceber a presenca de fumaca.",
  "nivel": "BASICO",
  "fonte": "CNEN"
}
```

Valores aceitos: `categoria` = SAUDE, INDUSTRIA, AGRICULTURA, PESQUISA, ENERGIA;
`nivel` = BASICO, INTERMEDIARIO, AVANCADO.

## Erros
Respostas de erro seguem o formato `{ status, mensagem, detalhes, timestamp }`
(400 para validação, 404 para id inexistente, 500 para erro inesperado).