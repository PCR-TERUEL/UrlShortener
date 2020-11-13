define({ "api": [
  {
    "type": "post",
    "url": "/link",
    "title": "Create short link",
    "name": "Create_short_link",
    "group": "ShortURL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "url",
            "description": "<p>URL you want to short.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "sponsor",
            "defaultValue": "sponsor",
            "description": "<p>Sponsor.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "uuid",
            "description": "<p>User Id.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "optional": false,
            "field": "201",
            "description": "<p>Link generated successfully.</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "401",
            "description": "<p>User does not exists.</p>"
          },
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "400",
            "description": "<p>Invalid or unreachable URL.</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/urlshortener/web/UrlShortenerController.java",
    "groupTitle": "ShortURL"
  },
  {
    "type": "post",
    "url": "/link",
    "title": "Get user links",
    "name": "Get_user_links",
    "group": "ShortURL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "url",
            "description": "<p>URL you want to short.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "sponsor",
            "defaultValue": "sponsor",
            "description": "<p>Sponsor.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "uuid",
            "description": "<p>User Id.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "optional": false,
            "field": "201",
            "description": "<p>Link generated successfully.</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "401",
            "description": "<p>User does not exists.</p>"
          },
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "400",
            "description": "<p>Invalid or unreachable URL.</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/urlshortener/web/UrlShortenerController.java",
    "groupTitle": "ShortURL"
  },
  {
    "type": "get",
    "url": "/{id:(?!link|index).*}",
    "title": "Shortened url",
    "name": "RedirectTo",
    "group": "ShortURL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Hash",
            "optional": false,
            "field": "id",
            "description": "<p>Shortened url unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "optional": false,
            "field": "OK",
            "description": "<p>Url Redirect.</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "UrlNotFound",
            "description": "<p>The url was not found.</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/urlshortener/web/UrlShortenerController.java",
    "groupTitle": "ShortURL"
  },
  {
    "type": "post",
    "url": "/login",
    "title": "User login",
    "name": "User_login",
    "group": "User",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "username",
            "description": "<p>Username.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "password",
            "description": "<p>Password.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "optional": false,
            "field": "202",
            "description": "<p>User login successful.</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "400",
            "description": "<p>Bad user parameters.</p>"
          },
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "203",
            "description": "<p>Wrong user or password.</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/urlshortener/web/UrlShortenerController.java",
    "groupTitle": "User"
  },
  {
    "type": "post",
    "url": "/register",
    "title": "User register",
    "name": "User_register",
    "group": "User",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "username",
            "description": "<p>Username.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "password",
            "description": "<p>Password.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "optional": false,
            "field": "OK",
            "description": "<p>User registered successfully.</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "400",
            "description": "<p>Bad user parameters.</p>"
          },
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "226",
            "description": "<p>Username already exists.</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/urlshortener/web/UrlShortenerController.java",
    "groupTitle": "User"
  }
] });