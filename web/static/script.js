const table = document.getElementById("table");
function redirectToAdd() {
  window.location.href = "pages/add.html";
}
fetch('localhost:50505/api/')
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => {
    data.forEach(product => {
        const row = document.createElement("div");
        const idSpan = document.createElement("span");
        const nameSpan = document.createElement("span");
        const unitsSpan = document.createElement("span");
        const priceSpan = document.createElement("span");

        idSpan.textContent=`${product.id}`;
        nameSpan.textContent=`${product.unitname}`;
        unitsSpan.textContent=`${product.units}`;
        priceSpan.textContent=`${product.price}`;
        table.appendChild(row);
      });
  })
  .catch(error => console.error('Error:', error));


  function searchproduct(){
    if(!document.getElementById("searchbar")){
      const nav = document.getElementById("topbar");
      const searchbar = document.createElement("input");
      searchbar.id = "searchbar";
      nav.appendChild(searchbar);
    }else{
      const searchbar = document.getElementById("searchbar");
      searchbar.remove();
    }
  }