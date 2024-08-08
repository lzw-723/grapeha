<script setup>

import {ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {fetchBookshelfById, fetchBooksInBookshelf, getBookCoverById} from "../api.js";
import Book from "../components/Book.vue";

const route = useRoute();
const router = useRouter();

const bookshelf = ref({
  id: null,
  title: "书架",
})
const books = ref([])

watch(() => route.params.id, (id) => {
  fetchBookshelfById(id).then(result => bookshelf.value = result)
  fetchBooksInBookshelf(id).then(result => books.value = result);
}, {immediate: true});

function goBook(id) {
  router.push("/books/" + id);
}

</script>

<template>
  <var-app-bar :title="bookshelf.title">
    <template #left>
      <var-button
        color="transparent"
        text-color="#fff"
        round
        text
        @click="$router.back()"
      >
        <var-icon name="chevron-left" :size="24"/>
      </var-button>
      <var-button
        color="transparent"
        text-color="#fff"
        round
        text
        @click="$router.push('/')"
      >
        <var-icon name="home" :size="24"/>
      </var-button>
    </template>

    <template #right>
      <var-menu>
        <var-button
          color="transparent"
          text-color="#fff"
          round
          text
        >
          <var-icon name="menu" :size="24"/>
        </var-button>

        <template #menu>
          <var-cell ripple>选项卡</var-cell>
          <var-cell ripple>选项卡</var-cell>
          <var-cell ripple>选项卡</var-cell>
        </template>
      </var-menu>
    </template>
  </var-app-bar>

  <ul class="bookshelf-list">
    <Book v-for="book in books"
          :title="book.name"
          :cover="getBookCoverById(book.id)"
          :author="book.author"
          @click="goBook(book.id)"></Book>
  </ul>
</template>

<style scoped>
.bookshelf-list {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 1rem;
}
</style>
