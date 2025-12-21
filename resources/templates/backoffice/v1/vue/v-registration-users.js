export default {
  template: `
    <div>
      <h1>Dashboard</h1>
      <button @click="load">Load API</button>
      <p>{{ msg }}</p>
    </div>
  `,
  data() {
    return { msg: "" };
  },
  methods: {
      async load() {
        const res = await fetch("/clear-cache");
        this.msg = (await res.json()).msg;
      }
    }
};