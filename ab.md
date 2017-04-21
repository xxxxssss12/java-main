# 市场偷额度活动提额接口

## 概述：

> 用于市场活动

## 接口url:
正式线：`http://model.data.wecash.net/receive/update_risk_sync`
预生产线：`http://model.data.wecash.net/preproduction/receive/update_risk_sync`

## 方法： POST

## 发送参数

用户编号  customer_id 和 渠道编号 source_id，以及额度信息user_amount(包含在evaluation里面）,service_type

```json
{
    "customer_id": "607329",
    "source_id": "100070",
    "service_type": "market_amount_increase",
    "evaluation": {
        "user_amount": 10000
    }
}
```

各参数含义定义如下

| 字段名字      | 字段含义                 | 字段类型 | 是否必须 |
|:--------------|:-------------------------|:---------|:---------|
| customer_id   | 用户编号，用户闪银自己的编号     | str      | 是       |
| source_id     | 渠道编号，C端固定100070 | str      | 是       |
| service_type  | C端交互业务标签，固定为"market_amount_increase"        | str       | 是       |
| evaluation  | 包含用户风险信息的dict      | dict       | 是       |
| user_amount   | 要提到的额度     | int     | 是       |

注：原额度与提升后额度差值不能超过2000，并且原额度需大于等于500

## 返回结果

成功返回结果：
```json
{
    "success": 1,
    "msg": "操作成功",
    "customer_id": "607329",
    "source_id": "100070"
}
```

失败范例
```json
{
    "success": 0,
    "msg": "fail push",
    "customer_id": "607329",
    "source_id": "100070"
}
```

```json
{
    "success": 0,
    "msg": "fail",
    "customer_id": "607329",
    "source_id": "100070"
}
```
