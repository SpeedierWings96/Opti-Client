#!/usr/bin/env python3
"""
Creates new logo images for Opti Client.
Creates simple orange gradient backgrounds similar to the original WURST logos but without text.
"""

from PIL import Image, ImageDraw
import os

def create_rounded_rectangle(size, color_start, color_end):
    """Create a rounded rectangle with gradient."""
    img = Image.new('RGBA', size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    width, height = size
    
    # Create gradient
    for x in range(width):
        # Calculate gradient color
        ratio = x / width
        r = int(color_start[0] * (1 - ratio) + color_end[0] * ratio)
        g = int(color_start[1] * (1 - ratio) + color_end[1] * ratio)
        b = int(color_start[2] * (1 - ratio) + color_end[2] * ratio)
        
        draw.line([(x, 0), (x, height)], fill=(r, g, b, 255))
    
    # Create rounded corners mask
    mask = Image.new('L', size, 0)
    mask_draw = ImageDraw.Draw(mask)
    
    corner_radius = min(height // 2, 20)
    mask_draw.rounded_rectangle([(0, 0), (width-1, height-1)], 
                                radius=corner_radius, fill=255)
    
    # Apply mask
    img.putalpha(mask)
    
    return img

def main():
    # Define colors (orange gradient similar to original)
    color_start = (255, 140, 0)  # Darker orange
    color_end = (255, 180, 50)   # Lighter orange
    
    # Create directory if needed
    assets_dir = "src/main/resources/assets/opti"
    os.makedirs(assets_dir, exist_ok=True)
    
    # Create opti_128.png (for the in-game HUD)
    logo_128 = create_rounded_rectangle((72, 18), color_start, color_end)
    logo_128.save(os.path.join(assets_dir, "opti_128.png"))
    print(f"Created {os.path.join(assets_dir, 'opti_128.png')}")
    
    # Create icon.png (for mod icon)
    icon = create_rounded_rectangle((512, 128), color_start, color_end)
    icon.save(os.path.join(assets_dir, "icon.png"))
    print(f"Created {os.path.join(assets_dir, 'icon.png')}")
    
    print("\nLogo images created successfully!")
    print("The logos are simple orange gradient backgrounds without text.")
    print("The Opti Client text will be rendered programmatically in the game.")

if __name__ == "__main__":
    try:
        main()
    except ImportError:
        print("Error: PIL/Pillow is not installed.")
        print("Please install it with: pip install Pillow")
        print("\nAlternatively, you can manually create orange gradient images:")
        print("- opti_128.png: 72x18 pixels")
        print("- icon.png: 512x128 pixels")
