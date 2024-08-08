import {getToken, getUsername} from "./store";

const token = getToken();
const username = getUsername();

function fetchUserToken(username, password) {
  return fetch("http://localhost:8080/api/v1/users/" + username + "/token", {
    method: "POST", body: JSON.stringify({password})
  })
    .then((response) => response.json()).then((data) => {
      if (data["code"] !== 200) {
        throw Error(data["msg"]);
      }
      token.value = data["data"];
      return token.value;
    });
}

function fetchBooksInBookshelf(id) {
  return fetch(`http://localhost:8080/api/v1/bookshelves/${id}/books`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  })
    .then((response) => response.json())
    .then((data) => data["data"]);
}

function fetchBooks() {
  return fetch("http://localhost:8080/api/v1/books", {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  })
    .then((response) => response.json())
    .then((data) => data["data"]);
}

function fetchBookById(id) {
  return fetch(`http://localhost:8080/api/v1/books/${id}`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  })
    .then((response) => response.json())
    .then((data) => data["data"]);
}

function getBookCoverById(id) {
  return `http://localhost:8080/api/v1/books/${id}/cover`;
}

function fetchBookshelves() {
  return fetch("http://localhost:8080/api/v1/bookshelves", {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  }).then((response) => response.json())
    .then((data) => data["data"]);
}

function fetchBookshelfById(id) {
  return fetch(`http://localhost:8080/api/v1/bookshelves/${id}`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  }).then((response) => response.json())
    .then((data) => data["data"]);
}

/**
 * 检查用户是否登录。如果登录，获取用户信息并返回；如果未登录或登录信息无效，抛出错误。
 * @throws {Error} 如果没有登录记录，抛出错误信息"没有登录记录！"。
 * @throws {Error} 如果登录状态码为 401，抛出错误信息"未登录！"。
 * @throws {Error} 如果获取用户信息的响应状态码不是 200-299 范围内，根据响应数据抛出对应的错误信息。
 * @returns {Object} 如果登录有效，返回用户数据。
 */
async function checkLogin() {
  console.log(token.value, username.value)
  if (token.value == null || username.value == null || username.value === "" || token.value === "") {
    throw Error("没有登录记录！");
  }
  let resp = await fetch(`http://localhost:8080/api/v1/users/${username.value}`, {
    headers: {
      authorization: `Bearer ${token.value}`,
    }
  });
  if (resp.status === 401) {
    throw Error("未登录！");
  }
  let data = await resp.json();
  if (data.code > 299 || data.code < 200) {
    throw Error(data.msg);
  }
  return data["data"];
}

export {
  fetchUserToken,
  fetchBookshelfById,
  fetchBookshelves,
  fetchBooksInBookshelf,
  fetchBookById,
  getBookCoverById,
  fetchBooks,
  checkLogin
};
