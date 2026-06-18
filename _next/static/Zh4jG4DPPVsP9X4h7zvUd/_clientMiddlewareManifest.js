self.__MIDDLEWARE_MATCHERS = [
  {
    "regexp": "^\\/fluentvoice-frontend(?:\\/(_next\\/data\\/[^/]{1,}))?\\/patient(?:\\/((?:[^\\/#\\?]+?)(?:\\/(?:[^\\/#\\?]+?))*))?(\\.json|\\.rsc|\\.segments\\/.+\\.segment\\.rsc)?[\\/#\\?]?$",
    "originalSource": "/patient/:path*"
  },
  {
    "regexp": "^\\/fluentvoice-frontend(?:\\/(_next\\/data\\/[^/]{1,}))?\\/therapist(?:\\/((?:[^\\/#\\?]+?)(?:\\/(?:[^\\/#\\?]+?))*))?(\\.json|\\.rsc|\\.segments\\/.+\\.segment\\.rsc)?[\\/#\\?]?$",
    "originalSource": "/therapist/:path*"
  },
  {
    "regexp": "^\\/fluentvoice-frontend(?:\\/(_next\\/data\\/[^/]{1,}))?\\/settings(?:\\/((?:[^\\/#\\?]+?)(?:\\/(?:[^\\/#\\?]+?))*))?(\\.json|\\.rsc|\\.segments\\/.+\\.segment\\.rsc)?[\\/#\\?]?$",
    "originalSource": "/settings/:path*"
  }
];self.__MIDDLEWARE_MATCHERS_CB && self.__MIDDLEWARE_MATCHERS_CB()