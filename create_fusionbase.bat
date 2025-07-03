@echo off
setlocal enabledelayedexpansion

REM 创建项目根目录
mkdir "D:\code\java\FusionBase"
cd /d "D:\code\java\FusionBase"

REM 创建后端目录结构
mkdir backend
cd backend
mkdir src\main\java\com\fusionbase
mkdir src\main\java\com\fusionbase\config
mkdir src\main\java\com\fusionbase\controller
mkdir src\main\java\com\fusionbase\service
mkdir src\main\java\com\fusionbase\service\impl
mkdir src\main\java\com\fusionbase\mapper
mkdir src\main\java\com\fusionbase\entity
mkdir src\main\java\com\fusionbase\dto
mkdir src\main\java\com\fusionbase\utils
mkdir src\main\java\com\fusionbase\exception
mkdir src\main\resources\mapper
mkdir src\main\resources\static
mkdir src\main\resources\templates
mkdir src\test\java\com\fusionbase

REM 创建后端关键文件
type nul > src\main\java\com\fusionbase\FusionBaseApplication.java
type nul > src\main\java\com\fusionbase\config\MinioConfig.java
type nul > src\main\java\com\fusionbase\config\RedisConfig.java
type nul > src\main\java\com\fusionbase\config\SwaggerConfig.java
type nul > src\main\java\com\fusionbase\config\WebMvcConfig.java
type nul > src\main\java\com\fusionbase\config\SecurityConfig.java
type nul > src\main\java\com\fusionbase\controller\AlbumController.java
type nul > src\main\java\com\fusionbase\controller\UserController.java
type nul > src\main\java\com\fusionbase\controller\AuthController.java
type nul > src\main\java\com\fusionbase\service\AlbumService.java
type nul > src\main\java\com\fusionbase\service\UserService.java
type nul > src\main\java\com\fusionbase\service\FileService.java
type nul > src\main\java\com\fusionbase\service\impl\AlbumServiceImpl.java
type nul > src\main\java\com\fusionbase\service\impl\UserServiceImpl.java
type nul > src\main\java\com\fusionbase\service\impl\FileServiceImpl.java
type nul > src\main\java\com\fusionbase\mapper\AlbumMapper.java
type nul > src\main\java\com\fusionbase\mapper\UserMapper.java
type nul > src\main\java\com\fusionbase\entity\Album.java
type nul > src\main\java\com\fusionbase\entity\Photo.java
type nul > src\main\java\com\fusionbase\entity\User.java
type nul > src\main\java\com\fusionbase\dto\LoginDTO.java
type nul > src\main\java\com\fusionbase\dto\PhotoDTO.java
type nul > src\main\java\com\fusionbase\dto\UserDTO.java
type nul > src\main\java\com\fusionbase\utils\JwtUtils.java
type nul > src\main\java\com\fusionbase\utils\R.java
type nul > src\main\java\com\fusionbase\utils\MinioUtil.java
type nul > src\main\java\com\fusionbase\exception\GlobalExceptionHandler.java
type nul > src\main\java\com\fusionbase\exception\CustomException.java
type nul > src\main\resources\application.yml
type nul > src\main\resources\mapper\AlbumMapper.xml
type nul > src\main\resources\mapper\UserMapper.xml
type nul > pom.xml
type nul > Dockerfile

cd ..

REM 创建前端目录结构
mkdir frontend
cd frontend
mkdir public
mkdir src\api
mkdir src\assets\css
mkdir src\assets\images
mkdir src\components\common
mkdir src\components\album
mkdir src\components\user
mkdir src\router
mkdir src\store\modules
mkdir src\utils
mkdir src\views

REM 创建前端关键文件
type nul > public\index.html
type nul > public\favicon.ico
type nul > src\api\album.js
type nul > src\api\auth.js
type nul > src\api\user.js
type nul > src\assets\css\global.css
type nul > src\components\common\Header.vue
type nul > src\components\common\Footer.vue
type nul > src\components\common\Loading.vue
type nul > src\components\album\AlbumUpload.vue
type nul > src\components\album\PhotoGrid.vue
type nul > src\components\album\PhotoPreview.vue
type nul > src\components\user\LoginForm.vue
type nul > src\components\user\RegisterForm.vue
type nul > src\router\index.js
type nul > src\store\modules\album.js
type nul > src\store\modules\user.js
type nul > src\store\index.js
type nul > src\utils\request.js
type nul > src\utils\auth.js
type nul > src\utils\validate.js
type nul > src\views\Home.vue
type nul > src\views\Album.vue
type nul > src\views\Profile.vue
type nul > src\views\Login.vue
type nul > src\views\Register.vue
type nul > src\App.vue
type nul > src\main.js
type nul > babel.config.js
type nul > vue.config.js
type nul > package.json
type nul > Dockerfile

cd ..

echo FusionBase 项目结构创建完成！
echo 项目位置: D:\code\java\FusionBase
echo.
echo 后续步骤:
echo 1. 使用 IntelliJ IDEA 打开 backend 目录作为 Maven 项目
echo 2. 在 frontend 目录运行: npm install
echo 3. 配置数据库和Minio服务
pause