# Dynamic IP Updater

## Problem Outline
If you are doing some self-hosting, you can sometimes have a dynamic IP Address given to you 
by your internet provider. This means that you manually have to update the DNS records of your
domain everytime this happens.

Unless we can automate this.

## How It Works
1. Get current IP address 
2. For each tracked domain, update its pointing IP Address if it's different from your public IP Address

## Supported DNS Provider
1. CloudFlare (https://developers.cloudflare.com/api/operations/dns-records-for-a-zone-patch-dns-record)

## Installation Guide 

### Docker
docker-compose.yaml
```
services:
  dynamicip:
    image: richardalmenares95/dynamicip:latest
    container_name: dynamicip
    environment:
      DYNAMICIP_DOMAINS_0_: "test.domain.uk"
      DYNAMICIP_DOMAINS_1_: "test2.domain.uk"
      DYNAMICIP_DOMAINS_2_: "test3.domain.uk"
      DYNAMICIP_CRON: "0 */2 * ? * *"
      CLOUDFLARE_APIKEY: "api_key"
      CLOUDFLARE_ZONEID: "zone_id"
```
