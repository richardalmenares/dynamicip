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
TODO
