# plugin-sitemap

Halo 2.0 的站点 Sitemap 链接生成插件

安装插件之后会提供一个 `http://your-host/sitemap.xml` 的链接作为站点的 Sitemap。

## 使用方式

1. 下载，目前提供以下两个下载方式：
    - GitHub Releases：访问 [Releases](https://github.com/halo-sigs/plugin-sitemap/releases) 下载 Assets 中的 JAR 文件。
    - Halo 应用市场：<https://halo.run/store/apps/app-QDFMI>。
2. 插件安装和更新方式可参考：<https://docs.halo.run/user-guide/plugins>。

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
./gradlew build
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
