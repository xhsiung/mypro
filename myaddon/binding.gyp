{
  "targets": [
    {
      "target_name": "addon",
      "sources": [ "addon.cpp", "myobject.cpp" ],
      "include_dirs": [
        "<!(node -e \"require('nan')\")"
      ]
    }
  ]
}
