const { chromium } = require('playwright');

(async () => {
  console.log('🚀 Starting OrangeHRM site verification...\n');
  
  const browser = await chromium.launch({ 
    headless: false,
    slowMo: 500 // Slow down for visibility
  });
  
  const context = await browser.newContext();
  const page = await context.newPage();
  
  try {
    // Navigate to the login page
    console.log('📍 Navigating to OrangeHRM login page...');
    await page.goto('https://opensource-demo.orangehrmlive.com/web/index.php/auth/login', {
      waitUntil: 'networkidle',
      timeout: 30000
    });
    
    console.log('✅ Page loaded successfully!\n');
    
    // Wait a bit for page to fully render
    await page.waitForTimeout(2000);
    
    // Check for input[name="username"]
    console.log('🔍 Checking for input[name="username"]...');
    const usernameInput = await page.locator('input[name="username"]');
    const usernameExists = await usernameInput.count() > 0;
    
    if (usernameExists) {
      const isVisible = await usernameInput.isVisible();
      console.log(`✅ input[name="username"] exists: ${usernameExists}`);
      console.log(`✅ input[name="username"] is visible: ${isVisible}\n`);
    } else {
      console.log('❌ input[name="username"] NOT FOUND\n');
    }
    
    // Check for input[name="password"]
    console.log('🔍 Checking for input[name="password"]...');
    const passwordInput = await page.locator('input[name="password"]');
    const passwordExists = await passwordInput.count() > 0;
    
    if (passwordExists) {
      const isVisible = await passwordInput.isVisible();
      console.log(`✅ input[name="password"] exists: ${passwordExists}`);
      console.log(`✅ input[name="password"] is visible: ${isVisible}\n`);
    } else {
      console.log('❌ input[name="password"] NOT FOUND\n');
    }
    
    // Capture actual HTML structure of the login form
    console.log('📋 Capturing login form HTML structure...\n');
    console.log('='.repeat(80));
    
    // Try to find the form container
    const formHTML = await page.evaluate(() => {
      // Try different selectors to find the login form
      const selectors = [
        'form',
        '[class*="login"]',
        '[class*="Login"]',
        '[class*="auth"]',
        '[class*="Auth"]',
        '.oxd-form'
      ];
      
      for (const selector of selectors) {
        const element = document.querySelector(selector);
        if (element) {
          return {
            selector: selector,
            html: element.outerHTML.substring(0, 2000), // Limit output
            inputs: Array.from(element.querySelectorAll('input')).map(input => ({
              tag: input.tagName,
              type: input.type,
              name: input.name,
              id: input.id,
              className: input.className,
              placeholder: input.placeholder
            }))
          };
        }
      }
      
      // If no form found, get all inputs on page
      return {
        selector: 'body',
        inputs: Array.from(document.querySelectorAll('input')).map(input => ({
          tag: input.tagName,
          type: input.type,
          name: input.name,
          id: input.id,
          className: input.className,
          placeholder: input.placeholder
        }))
      };
    });
    
    console.log('Form Container:', formHTML.selector);
    console.log('\nInput Elements Found:');
    console.log(JSON.stringify(formHTML.inputs, null, 2));
    
    if (formHTML.html) {
      console.log('\nForm HTML (truncated):');
      console.log(formHTML.html);
    }
    
    console.log('='.repeat(80));
    
    // Summary
    console.log('\n📊 SUMMARY:');
    console.log('='.repeat(80));
    console.log(`✅ Site is accessible: YES`);
    console.log(`✅ Page loads successfully: YES`);
    console.log(`✅ input[name="username"] exists: ${usernameExists}`);
    console.log(`✅ input[name="password"] exists: ${passwordExists}`);
    
    if (formHTML.inputs.length > 0) {
      console.log('\n🎯 CORRECT SELECTORS:');
      formHTML.inputs.forEach((input, index) => {
        console.log(`\nInput ${index + 1}:`);
        if (input.name) console.log(`  - name: input[name="${input.name}"]`);
        if (input.id) console.log(`  - id: #${input.id}`);
        if (input.placeholder) console.log(`  - placeholder: input[placeholder="${input.placeholder}"]`);
        if (input.className) console.log(`  - class: .${input.className.split(' ').join('.')}`);
      });
    }
    console.log('='.repeat(80));
    
    // Keep browser open for 5 seconds to see the page
    console.log('\n⏳ Keeping browser open for 5 seconds...');
    await page.waitForTimeout(5000);
    
  } catch (error) {
    console.error('❌ Error:', error.message);
  } finally {
    await browser.close();
    console.log('\n✅ Test completed!');
  }
})();
