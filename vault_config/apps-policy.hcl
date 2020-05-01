# Read-only permit
path "kv/secret/my-service-a" {
  capabilities = [ "read", "write"  ]
}
