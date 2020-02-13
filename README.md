= Starter

== springboot 结合 vertx-web , 尝试让springBoot的开发效率和vertx的运行效率结合起来
  目的是像使用 springboot web starter 一样使用vertx,
  目前可以使用的注解如下：
```
spring-web 注解中的
@RestController ， @Controller ， @RequestMapping  ， @RequestParam， @RequestBody
@PathVariable， @GetMapping. @PutMapping, @PostMapping, @DeleteMapping

spring-validator 注解支持
```
  新加的注解: @AsyncHandler 使用 vertx 异步模式
  默认使用 vertx 阻塞模式，即 executeBlocking()
== 开始

```
   <dependency>
    	<groupId>com.github.zhengyuyzhao</groupId>
    	<artifactId>vertx-spring-boot-starter</artifactId>
    	<version>1.0.7</version>
   </dependency>
```
== application.yml
```
   vert:
     port: 8000
     work-pool-size: 100
     instance: 10
```

== Controller demo
```
    @RestController
    @RequestMapping("/test")
    public class Tes1tController {
    }
```

== param demo
```
    @RequestMapping(value = "/test3/:id", method = RequestMethod.PUT)
    public Map post(@RequestParam(value = "id") @NotNull int id,
                    @RequestParam(value = "qq", required = false, defaultValue = "333") int qq,
                    @RequestBody() Map map) {
                    }
```
```
    @RequestMapping(value = "/test4", method = RequestMethod.GET)
        public Map post4(RoutingContext context) {
    }
```
== Interceptor demo
```
@Component
    public class Interceptor implements VertxHandlerInterceptor {
      @Override
      public boolean preHandle(RoutingContext context, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if(handlerMethod.getMethod().isAnnotationPresent(ResponseBody.class)){
          context.response().end("not support responsebody");
          return false;
        }
        return true;
      }
    }

```

== async demo
```
    @AsyncHandler
    @RequestMapping(value = "/test5", method = RequestMethod.GET)
    public void post5(RoutingContext context, @NotNull String id) {
        mongoService.getMusics(context);
    }

```


