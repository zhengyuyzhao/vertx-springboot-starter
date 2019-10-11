package com.zzy.starter;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
  private static final Logger logger = LoggerFactory.getLogger(TestController.class);

  @RequestMapping(value = "/test2/:id", method = RequestMethod.GET)
  public List list2(@RequestParam(value = "id") int id) {
    List list = new ArrayList();
    list.add(new VertxConfig());
    list.add(id);
    return list;
  }

  @RequestMapping(value = "/test2/${id:22}", method = RequestMethod.GET, produces = {})
  public List list22(@RequestParam(value = "id") int id) {
    List list = new ArrayList();
    list.add(new VertxConfig());
    list.add(id);
    return list;
  }

  @RequestMapping(value = "/list/{page}", method = RequestMethod.GET)
  public String findByService(@RequestParam(name = "app_code") String appcode,
                              @PathVariable int page,
                              @RequestParam(defaultValue = "23") int size,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false, defaultValue = "-1") long categoryId) {
    logger.info("list--{}",Thread.currentThread().getName());
    return "222";
  }

  @RequestMapping(value = "/test3/:id", method = RequestMethod.PUT)
  public Map post(@RequestParam(value = "id") int id,
                  @RequestParam(value = "qq", required = false, defaultValue = "333") int qq,
                  @RequestBody() Map map) {
    map.put("id", id);
    map.put("qq", qq);
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/test4", method = RequestMethod.GET)
  public Map post4(RoutingContext context) {
    Map map = new HashMap();
    map.put("context", context.data());
    map.put("path", context.request().path());
    context.response().end(Json.encode(map));
    return map;
  }
}
