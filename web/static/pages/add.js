// Configuration object for API settings
const API_CONFIG = {
    endpoint: 'http://192.168.1.7:50505/api/',
    headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': 'http://192.168.1.7:50505/api/',
        'Access-Control-Allow-Methods': 'POST, GET, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type'
    }
};

const serializeFormData = form => {
    const formData = new FormData(form);
    return Object.fromEntries(formData);
};

async function submitFormData(data) {
    try {
        const preflightResponse = await fetch(API_CONFIG.endpoint, {
            method: 'OPTIONS',
            headers: API_CONFIG.headers,
            mode: 'cors', 
            credentials: 'include', 
        });

        if (!preflightResponse.ok) {
            throw new Error(`Preflight request failed: ${preflightResponse.status}`);
        }

        const response = await fetch(API_CONFIG.endpoint, {
            method: 'POST',
            headers: API_CONFIG.headers,
            mode: 'cors', 
            credentials: 'include', 
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        console.log('Success:', result);
        return result;
    } catch (error) {
        console.error('Failed to submit form:', error);
        if (error.message.includes('CORS')) {
            console.error('CORS error detected. Please check server configuration.');
        }
        throw error;
    }
}

function initializeForm() {
    const form = document.querySelector('#form');
    
    if (!form) {
        console.warn('Form element not found');
        return;
    }

    form.addEventListener('submit', async function(event) {
        event.preventDefault(); 

        try {
            const formData = serializeFormData(this);
            console.log('Form Data:', formData);
            const submitButton = this.querySelector('button[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = 'Submitting...';
            }

            const response = await submitFormData(formData);
            
            alert('Form submitted successfully!');
            
        } catch (error) {
            if (error.message.includes('CORS')) {
                alert('Cross-origin request failed. Please check server configuration.');
            } else {
                alert('Failed to submit form. Please try again.');
            }
            console.error('Submission error:', error);
        } finally {
            if (submitButton) {
                submitButton.disabled = false;
                submitButton.textContent = 'Submit';
            }
        }
    });
}
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeForm);
} else {
    initializeForm();
}