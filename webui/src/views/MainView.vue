<script setup>
import {ref} from "vue";
import {useRouter} from "vue-router";
import {getToken, getUsername} from "../store";
import {checkLogin, fetchBooks, fetchBookshelves} from "../api";
import Bookshelf from "../components/Bookshelf.vue";

const router = useRouter();

let bookshelves = ref([]);
let books = ref([]);

function getBookshelves() {
  fetchBookshelves().then(r => bookshelves.value = r).catch(err => console.log(err));
}

async function getBooks() {
  try {
    books.value = await fetchBooks();
  } catch (err) {
    console.log("获取books失败");
  }
}

async function getBookShelves() {
  console.log("getBookShelves");
}

function getCoverUrl(id) {
  return "/api/v1/books/" + id + "/cover";
}

function goBookshelf(id) {
  router.push("/bookshelves/" + id);
}

function goBook(id) {
  router.push("/books/" + id);
}

function goLogin() {
  router.push("/login");
}

function logout() {
  getUsername().value = "";
  getToken().value = "";
  router.push("/login");
}

checkLogin()
  .then((result) => {
    console.log("成功")
    console.log(result);
  })
  .catch((err) => {
    // console.error(err);
    goLogin();
  });

getBookshelves();
getBooks();
</script>

<template>
  <var-app-bar title="GrapeHA">
    <template #right>
      <var-button color="transparent"
                  text-color="#fff"
                  round
                  text
                  @click="logout">
        <var-icon name="power" :size="24"/>
      </var-button>
    </template>
  </var-app-bar>

  <var-divider description="书架区"/>

  <ul class="bookshelves-container">
    <div class="bookshelf" v-for="bookshelf in bookshelves">
      <Bookshelf
        :title="bookshelf.title"
        @click="goBookshelf(bookshelf.id)">
      </Bookshelf>
    </div>
  </ul>
  <var-divider description="文字描述"/>
  <ul style="display: flex; flex-direction: row; flex-wrap: wrap">
    <div
      v-for="book in books"
      style="width: 200px; height: 300px; margin: 1rem"
    >
      <var-card
        :src="getCoverUrl(book.id)"
        :title="book.name"
        :subtitle="book.author"
        @click="goBook(book.id)"
      />
    </div>
  </ul>
</template>

<style scoped>
.bookshelves-container {
  white-space: nowrap;
  overflow-x: scroll;
}

.bookshelf {
  display: inline-block;
  margin: 0.5rem;
}
</style>
