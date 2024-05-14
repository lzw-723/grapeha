import { createAlova } from "alova";
import GlobalFetch from "alova/GlobalFetch";
import { getToken, getUsername } from "./store";

const alovaInstance = createAlova({
  requestAdapter: GlobalFetch(),
});

const token = getToken();
const username = getUsername();

function fetchBooks() {
  return alovaInstance
    .Get("http://localhost:8080/api/v1/books", {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    })
    .then((response) => response.json())
    .then((data) => data["data"]);
}

function fetchBookById(id) {
  return alovaInstance
    .Get(`http://localhost:8080/api/v1/books/${id}`, {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    })
    .then((response) => response.json())
    .then((data) => data["data"]);
}

async function checkLogin() {
  if (token.value == null || username.value == null) {
    throw Error("没有登录记录！");
  }
  let resp = await alovaInstance.Get(
    "http://localhost:8080/api/v1/users/" + username.value,
    {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    }
  );
  if (resp.status === 401) {
    throw Error("未登录！");
  }
  let data = await resp.json();
  if (data.code > 299 || data.code < 200) {
    throw Error(data.msg);
  }
  return data["data"];
}

export { fetchBookById, fetchBooks, checkLogin };
