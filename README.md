# plugin-sitemap

Halo 2.0 的站点 Sitemap 链接生成插件。

## 开发环境

```bash
git clone git@github.com:halo-dev/plugin-sitemap.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/plugin-sitemap.git
```

```bash
cd path/to/plugin-sitemap
```

```bash
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
```

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    classes-directories:
      - "build/classes"
      - "build/resources"
    lib-directories:
      - "libs"
    fixedPluginPath:
      - "/path/to/plugin-sitemap"
```

## 使用方式

1. 在 [Releases](https://github.com/halo-dev/plugin-sitemap/releases) 下载最新的 JAR 文件。
2. 在 Halo 后台的插件管理上传 JAR 文件进行安装。
3. 启动插件之后，即可通过 `https://your-host/sitemap.xml` 来访问站点地图。
