{
  "targets": [
    {
      "target_name": "myasync",
      "sources": [ "myasync.cpp", "async.cpp" ],
      "include_dirs": [
        "<!(node -e \"require('nan')\")"
      ]
    }
  ]
}

