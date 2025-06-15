## 🚀 README Miau&Auau 🚀

Este README fornece instruções sobre como instalar e executar este projeto.

E sim, o Swagger UI está rodando lindamente em! 😉

### 🛠️ Pré-requisitos 🛠️

Antes de botar a mão na massa, você precisará ter as seguintes ferramentas instaladas na sua máquina:

* **☕ Java Development Kit (JDK):** Versão 17.
    É **crucial** que a variável de ambiente `JAVA_HOME` esteja configurada direitinho e que o comando `java -version` retorne a versão 17.

* **💡 IntelliJ IDEA:** Qualquer edição (Community ou Ultimate) vai dar conta do recado!

* **🐬 MySQL:** Versão 8.0.
    Certifique-se de que o servidor MySQL esteja rodando a todo vapor e que você tenha as credenciais (**usuário** e **senha**) para acessar o banco de dados.

### ⚙️ Instalação ⚙️

Siga estas etapas para deixar tudo tinindo:

1.  **⬇️ Clone o repositório:**

    ```bash
    git clone [https://github.com/boliveira01/backend-miaeauau](https://github.com/boliveira01/backend-miaeauau)
    cd backend-miaeauau
    ```

2.  **📂 Abra o projeto no IntelliJ IDEA:**

    * Abra o seu IntelliJ IDEA.
    * Clique em "Open" ou "Open Project".
    * Navegue até a pasta onde você clonou o projeto e selecione-a.
    * O IntelliJ IDEA deve identificar o projeto como um projeto Java automaticamente.

3.  **⚙️ Configure o banco de dados MySQL:**

    * Verifique se o servidor MySQL está ligado.

    * Crie um novo banco de dados para o projeto, se ainda não existir. Use um cliente MySQL como o MySQL Workbench ou a linha de comando:

        ```sql
        CREATE DATABASE miaueauau CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
        ```

    * Crie um usuário MySQL com as **permissões necessárias** para acessar o banco de dados. Exemplo:

        ```sql
        CREATE USER 'miau'@'localhost' IDENTIFIED BY 'auau';
        GRANT ALL PRIVILEGES ON miaueauau.* TO 'miau'@'localhost';
        FLUSH PRIVILEGES;
        ```

4.  **✍️ Configure as informações de conexão com o banco de dados:**

    * Procure o arquivo de configuração do banco de dados no seu projeto. Geralmente, ele se encontra em um arquivo como `application.properties` ou `application.yml` dentro da pasta de recursos (`src/main/resources`).

    * Edite este arquivo com os dados de conexão do seu banco de dados MySQL:

        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/miaueauau?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo
        spring.datasource.username=miau
        spring.datasource.password=auau
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        ```

        Lembre-se de substituir `miaueauau`, `miau` e `auau` pelas informações corretas. Ajuste o `serverTimezone` se precisar.

5.  **▶️ Execute o projeto:**

    * No IntelliJ IDEA, vá até a **classe principal** `ClinicaVeterinariaApplication`.
    * Clique com o botão direito nessa classe e selecione "Run".
    * O IntelliJ IDEA vai compilar e rodar o projeto. Fique de olho nos logs de inicialização no console! 👀

### ➡️ Próximos Passos ➡️

* 🌐 **Acesse o Swagger UI:** Para interagir com a API e ver a documentação em tempo real, acesse o seguinte endereço no seu navegador: http://localhost:8080/swagger-ui/index.html#/
* 📚 Dê uma olhada na **documentação do projeto** para entender todas as funcionalidades.
* 🧐 Monitore os logs da aplicação para verificar se há algum erro ou aviso.
* Explore o código no IntelliJ IDEA para sacar a estrutura e a lógica do projeto.

Obrigado!
