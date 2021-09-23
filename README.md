# 個人財務管理平台

功能包括:收支與股票投資紀錄

## 一、後端開發環境設置

工具: IDEA-2019.3、Springboot-2.5.4、Springcloud-2020.0.3、MongoDB。

### 1. 編寫註冊中心模組

註冊中心模組(server-eureka)父文件裡的 pom 內容如下:

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1632311292/financialManager/pom-parent_sd8ng2.png)

#### 1.1 引入依賴:

``` xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```	

#### 1.2 於 application.yml 編寫配置

``` yml
server:
  port: 8761	
spring:
  application: eureka-server
eureka:
  instance:
    hostname: localhost
  client:
    defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  register-with-eureka: false
  fetch-registry: false
```          

#### 1.3 編寫主啟動類
並於主啟動類上添加 @EnableEurekaServer 註解

``` Java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplcation {
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

至此,就完成了註冊中心模組的編寫,開啟 localhost:8761,可見到如下畫面:

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1629808917/financialManager/eureka_d28zcv.jpg)


### 2. 編寫配置中心模組

#### 2.1 引入依賴

```xml
<dependencies>
   <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-config-server</artifactId>
   </dependency>
   <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
   </dependency>
</dependencies>	
```

#### 2.2 於 application.yml 編寫配置
並於這個module src/main/resources 下新增一個檔名為 <strong style="color:#DD5145">shared </strong>的資料夾,用以放置provider模組與consumer模組各自的配置。

```yml
server:
  port: 8762
spring:
  application:
    name: configserver
  profiles:
    active: native #自本地讀取配置
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/shared
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```
 
#### 2.3 編寫主啟動類
並於主啟動類上添加 @EnableConfigServer 註解

```Java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class,args);
	}
}
```

完成配置模組編寫,啟動配置模組後,可觀察到配置模組已存在於註冊中心。

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1629810012/financialManager/configserver_edpqu4.jpg)

### 3. 編寫服務提供者模組

#### 3.1 引入依賴

```xml
<!-- 註冊中心客戶端 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<!-- 配置中心 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<!--有了bootstrap,spring會將bootstrap.xml列為優先讀取,才能引導讀取這個module在config的配置-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
<!-- 服務調用feign -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
...
```

#### 3.2 於 bootstrap.yml 編寫配置文件位置等資料

```yml
# 運用bootstrap.yml(檔名不能改),來讀取配置文件
spring:
  application:
    name: stock
  profiles:
    active: dev
  cloud:
    config:
      uri: http://localhost:8762 #配置文件位置
      fail-fast: true
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true      
```      

#### 3.3 於 配置中心模組的shared目錄下編寫 stock-dev.yml 配置文件

```yml
server:
  port: 8070
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/financialManager
```

#### 3.4 編寫主啟動類
  並添加@EnableEurekaClient註解

```Java
@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = {"com.chiczu.fmoney.stockprovider",
                "com.chiczu.fmoney.utils"})
public class StockProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockProviderApplication.class,args);
    }
}
```

至此完成了一個服務提供者的編寫,啟動後,可觀察到此服務提供者(stock:在配置文件中設置的名稱)已存在於註冊中心。

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1632313952/financialManager/stock_provider_qxa2s5.jpg)  

### 4. 編寫服務消費者模組

#### 4.1 引入依賴

```xml
	<!-- eurekaclient -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!-- 配置中心 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>
    <!-- 整合feign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
```    

#### 4.2 於 bootstrap.yml 編寫配置文件位置等資料

```yml
spring:
  application:
    name: clientconsumer
  profiles:
    active: dev
  cloud:
    config:
      uri: http://localhost:8762 #配置中心通訊埠
      fail-fast: true
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

#### 4.3 於 配置中心模組的shared目錄下編寫 clientconsumer-dev.yml 配置文件      

```yml
server:
  port: 8010
```

#### 4.4 編寫主啟動類
並於主啟動類上添加 @EnableEurekaClient 註解

```Java
@SpringBootApplication
@EnableEurekaClient
public class ClientConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientConsumerApplication.class,args);
    }
}
```

至此完成了一個服務提供者的編寫,啟動後,可觀察到此服務消費者(clientconsumer:在配置文件中設置的名稱)已存在於註冊中心。

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1632314460/financialManager/client_consumer_ua1wfs.jpg)

### 5. 編寫feign模組

#### 5.1 引入依賴

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

```
#### 5.2 編寫消費模組調用介面(interface)

```Java
@FeignClient("stock")
public interface StockFeign {

    @GetMapping("/stock/getPort")
    public String getPort();
  ...  

```
注意事項:
1.介面上的註解@FeignClient括弧裡的值,指的是服務提供端的名稱(即配置文件中spring.application.name 的值)。
2.上面方法(測試用)的路徑,要寫該被調用方法在provider的完全路徑。

#### 5.3 在服務消費模組的主啟動類上,添加@EnableFeignClients註解
並寫入feign模組的位置

```Java
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.chiczu.fmoney.feign")
public class ClientConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientConsumerApplication.class,args);
    }
}
```
  
#### 5.4 測試 feign 遠端調用功能

目標:consumer經由 feign interface 裡的方法,調用provider,進而獲取數據。

編寫provider的測試用方法 getPort():

```Java
@RestController
@RequestMapping("/stock")
public class StockProviderHandler {
	
	@Value("${server.port}")
	private String port;
	
	@GetMapping("/getPort")
	public String getPort() {
		return port;
	}
}
```

編寫consumer的測試用方法 getPort():

```Java
@RestController
@RequestMapping("/stock")
public class StockController {
	
	@Autowired
	private StockFeign stockFeign;
	
	@GetMapping("/port")
	public String getPort() {
		return stockFeign.getPort();
	}
}
```

在瀏覽器上輸入consumer訪問路徑(localhost:8010/stock/port),能獲取這個provider設置的通訊埠號(8070),就表示通過feign調用遠端方法測試成功。


至此後端開發環境編寫完成。

## 二、前端開發環境設置

工具: VSCode:1.60.1、vue:2.6.11、vue-router:3.5.2、vuex:3.6.2、axios:0.21.1、bootstrap:4.5.3、bootstrap-vue:2.21.2         

### 1. 建立專案

(已安裝好Node與Vue)

於VSCode的終端機下,在目地資料夾中以「vue create projectName」的指令創建新專案

### 2. 安裝工具

在專案的路徑下,以「npm install 工具名稱」的指令安裝各個工具。

### 3. 編寫 router的 index.js

```js
// 引入vue-router
import VueRouter from 'vue-router'
// 引入組件
import MainView from '../MainView.vue'
// 創建實例
export default new VueRouter({
  routes:[
    {
      path:'/',
      name:'MainView',
      component:MainView,
    },
  ]
})

```

### 4. 編寫 vuex的 index.js

以模組化方式編寫,之後依需求(數據共用情形)為組件編寫獨立的js檔。

```js
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  modules:{
    
  }
})

```

### 5. 在 main.js中配置vue-router、vuex與bootstrap-vue

```Javascript
import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import router from './router/index'
import store from './store/index'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
// Import Bootstrap an BootstrapVue CSS files (order is important)
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.config.productionTip = false

Vue.use(VueRouter)
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

new Vue({
  render: h => h(App),
  router,
  store
}).$mount('#app')

```

### 6. 為處理跨域問題,設置代理

在專案的根目錄下,創建一個 vue.config.js 檔,內容如下

```js
module.exports = {
  devServer:{
    proxy:{
      '/api':{ // 請求前綴有api,就轉發給「http://localhost:8010」伺服器
        target:'http://localhost:8010',
        pathRewrite:{'^/api':''}, // 將api(含)之前的前綴,都轉成空字串
        ws:true, // 用於支持websocket,默認true
        changeOrigin:true, // 默認true,用於控制請求頭中的host值
      },
    }
  }
}

```
至此,完成了前端開發環境設置。

## 三、專案完成後,主要頁面圖檔

1.收支情形

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1632366385/financialManager/%E6%94%B6%E6%94%AF_kt1mwj.jpg)

2.股票庫存登錄

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1632366385/financialManager/%E8%82%A1%E7%A5%A8%E5%BB%BA%E6%AA%94_qhhqhq.jpg)

3.量價圖

![image](https://res.cloudinary.com/hhmxekcfc/image/upload/v1632366385/financialManager/%E9%87%8F%E5%83%B9%E5%9C%96_fkyki3.jpg)
