import os
import re
import base64
import urllib.request
import urllib.parse

def compile_mermaid_code(mermaid_code):
    # Clean up the code a bit
    mermaid_code = mermaid_code.strip()
    
    # Base64 encode the code
    code_bytes = mermaid_code.encode('utf-8')
    base64_bytes = base64.b64encode(code_bytes)
    base64_string = base64_bytes.decode('utf-8')
    
    # URL for mermaid.ink
    url = f"https://mermaid.ink/img/{base64_string}"
    
    try:
        req = urllib.request.Request(
            url, 
            headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}
        )
        with urllib.request.urlopen(req) as response:
            return response.read()
    except Exception as e:
        print(f"Error compiling diagram: {e}")
        return None

def process_file(file_path):
    print(f"Processing {file_path}...")
    if not os.path.exists(file_path):
        print(f"File {file_path} does not exist. Skipping.")
        return
        
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # Pattern to find lstlisting blocks containing mermaid-like code
    pattern = re.compile(
        r'\\begin\{lstlisting\}\[language=(?:SQL|mermaid)[^\]]*\](.*?)(\\end\{lstlisting\})',
        re.DOTALL
    )
    
    os.makedirs('images', exist_ok=True)
    
    def replace_block(match):
        code_block = match.group(1).strip()
        
        # Check if it looks like Mermaid
        is_mermaid = False
        diagram_name = "diagram"
        
        # 1. Check for manual name override comment %% name: some_name
        name_match = re.search(r'%%\s*name:\s*([\w_]+)', code_block)
        if name_match:
            is_mermaid = True
            diagram_name = name_match.group(1)
        # 2. Substring fallbacks for automatic naming
        elif "flowchart" in code_block or "graph" in code_block:
            is_mermaid = True
            if "Environnement Externe" in code_block or "Externe" in code_block:
                diagram_name = "context_diagram"
            elif "com.example.account.modules.facturation" in code_block or "domain (C" in code_block:
                diagram_name = "package_diagram"
            else:
                diagram_name = "use_case"
        elif "classDiagram" in code_block:
            is_mermaid = True
            if "FacturationUseCase" in code_block:
                diagram_name = "analysis_class_diagram"
            else:
                diagram_name = "class_diagram"
        elif "sequenceDiagram" in code_block:
            is_mermaid = True
            if "validerFacture" in code_block or "createFacture" in code_block:
                diagram_name = "seq_validation"
            else:
                diagram_name = "seq_compta"
        elif "stateDiagram" in code_block:
            is_mermaid = True
            diagram_name = "state_diagram"
            
        if is_mermaid:
            print(f"Found Mermaid diagram: {diagram_name}")
            img_data = compile_mermaid_code(code_block)
            if img_data:
                img_path = f"images/{diagram_name}.png"
                with open(img_path, 'wb') as img_f:
                    img_f.write(img_data)
                print(f"Saved {img_path}")
                return f"\\includegraphics[width=0.9\\textwidth]{{{img_path}}}"
            else:
                print(f"Failed to compile {diagram_name}")
                return match.group(0) # Keep original if failed
        else:
            return match.group(0) # Keep original if not mermaid
            
    new_content = pattern.sub(replace_block, content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    print("Done processing file.")

if __name__ == '__main__':
    # Process both files
    process_file('chapitre2.tex')
    process_file('chapitre3.tex')
