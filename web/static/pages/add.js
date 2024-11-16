document.addEventListener('DOMContentLoaded', function (){
    var from = document.getElementById("form");
    if(typeof form !== 'undefined' && form !== null) {
        console.log("Form Not Null");
        form.addEventListener('submit', function(event) {
            const formData = new FormData(event.target);
            const data = {};
            formData.forEach((value, key) => {
                data[key] = value;
            });
            console.log('Form Data:', data);
            fetch('localhost:50505/api/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                console.log('Success:', data);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        });
    }    
})