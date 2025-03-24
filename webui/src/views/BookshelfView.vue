<script setup>

import {ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {fetchBookshelfById, fetchBooksInBookshelf, getBookCoverById} from "../api.js";
import Book from "../components/Book.vue";
import Appbar from "../components/Appbar.vue";

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

  <Appbar :title="bookshelf.title"/>

  <ul class="p-2 grid grid-cols-5 gap-2">
    <Book v-for="book in books"
          :title="book.name"
          :cover="getBookCoverById(book.id)"
          :author="book.author"
          @click="goBook(book.id)"></Book>
  </ul>
</template>
