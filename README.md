# plugin-sitemap
> 声明： 本插件从 [plugin-sitemap v1.0.2](https://github.com/halo-sigs/plugin-sitemap) 代码升级而来，不进行自动应用商店发布

Halo 2.0 的站点 Sitemap 链接生成插件。

## 开发环境

```bash
git clone https://redirect.cnkj.site:18092/gitea/plugin-sitemap.git
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

1. 在 [Releases](https://redirect.cnkj.site:18092/gitea/plugin-sitemap/releases) 下载最新的 JAR 文件。
2. 在 Halo 后台的插件管理上传 JAR 文件进行安装。
3. 如果需要自定义配置，请在安装插件后选择“否”不立即启动插件
   1. 进入插件“基本设置”中，根据实际需求填写相关配置内容
4. 启动插件，即可通过 `https://<domain>/sitemap.xml` 来访问站点地图
