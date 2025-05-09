# みらい バクエンド（mirai backend）

> Spring Boot をベースにしたバックエンド管理システムです。  
> ユーザー管理・認証・データ統計・メニュー管理・API管理・コード生成・辞書管理などの機能を提供します。  
> 基于Spring Boot的总后台管理系统。支持用户管理、权限认证、数据统计、菜单管理、API 管理、字典管理、代码生成等后台功能模块。


## モジュール（API Module）

| 项目                       | 説明                 |
|--------------------------|--------------------|
| `mirai-dependencies`     | Maven の依存関係バージョン管理 |
| `mirai-framework`        | Java フレームワークの拡張    |
| `mirai-server`           | 管理者画面とユーザーアプリのサーバー |
| `mirai-module-system`    | システム機能のモジュール       |
| `mirai-module-infra`     | インフラ機能のモジュール       |
| `mirai-module-employees` | 人事機能のモジュール         |



## 技術スタック（使用技术栈）

- Java 17 / 11
- Spring Boot 2.7.18
- MyBatis Plus（マイバティス拡張）
- Redis
- PostgreSQL
- Maven
- Docker
- Lombok, Swagger など

## クイックスタート（快速开始）

### 環境要件（环境要求）

- JDK 17 以上
- Maven 3.8 以上
- PostgreSQL
- Redis（必要に応じて）
- IntelliJ IDEA + Lombok プラグイン


### 事前準備（启动前准备）

- Redis：
> Redis を Docker で起動します（建议使用 Docker 启动 Redis）
  #### Docker インストール（安装 Docker）：

   - [Docker 公式サイト](https://docs.docker.com/get-docker/) より環境に応じたバージョンをインストールしてください。
   - Windows / Mac 用户：推荐使用 Docker Desktop

  #### Redis 起動用スクリプト（Redis 启动脚本）：

  ```bash
  docker run -d \
  --name mirai-redis \
  -p 6379:6379 \
  redis:7.4.6
   ```
### 起動手順（启动步骤）

1. プロジェクトをクローン（克隆项目）：
   ```bash
   git clone https://github.com/andydev920/mirai-backend.git
   ```
2. IntelliJ IDEA（2025.1）でプロジェクトを開く（用 IntelliJ IDEA 打开项目）：

- 公式サイトからダウンロード
   https://www.jetbrains.com/idea/download/

- 起動後、Open を選択し、クローンしたフォルダ（mirai-backend）を開きます。 pom.xml を認識して、自動的に Maven プロジェクトとしてインポートされます。

3. 必要な依存関係をダウンロード（下载依赖库）：

- 画面右下に「Mavenプロジェクトの読み込み」通知が表示されたら、[Import Changes] をクリックします。 または、画面右側の Maven タブ → [Reload All Maven Projects] ボタンをクリックします。

- プロジェクトをビルド（构建项目）： 
  - メニューから Build → Build Project を選択します。 
  - またはショートカット Ctrl + F9（Macは Cmd + F9）でビルド。

- アプリケーションを起動（启动应用）：
  - mirai-server/src/main/java/jp/mirai/server/MiraiServerApplication.java を右クリック → Run 'MiraiServerApplication.main()' を選択。

- ログに 「mirai run successful！」 と表示されたら、起動成功です。