#!/bin/bash

# Script to generate pitch-shifted variants of an audio file
# Usage: ./generate_pitch_variants.sh <input_file> [output_directory]

set -e

# Check if ffmpeg is installed
if ! command -v ffmpeg &> /dev/null; then
    echo "Error: ffmpeg is required but not installed."
    echo "Please install ffmpeg first."
    exit 1
fi

# Check arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 <input_file> [output_directory]"
    echo ""
    echo "Generates 4 pitch-shifted versions of the input audio file:"
    echo "  - filename_plus_2.ext (+2% higher pitch)"
    echo "  - filename_plus_4.ext (+4% higher pitch)"
    echo "  - filename_minus_2.ext (-2% lower pitch)"
    echo "  - filename_minus_4.ext (-4% lower pitch)"
    echo ""
    echo "Arguments:"
    echo "  input_file       Path to the input audio file"
    echo "  output_directory Optional output directory (defaults to same as input)"
    exit 1
fi

INPUT_FILE="$1"
OUTPUT_DIR="${2:-$(dirname "$INPUT_FILE")}"

# Check if input file exists
if [ ! -f "$INPUT_FILE" ]; then
    echo "Error: Input file '$INPUT_FILE' does not exist."
    exit 1
fi

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Extract filename without extension and extension
BASENAME=$(basename "$INPUT_FILE")
FILENAME="${BASENAME%.*}"
EXTENSION="${BASENAME##*.}"

echo "Generating pitch-shifted variants of '$INPUT_FILE'..."
echo "Output directory: $OUTPUT_DIR"
echo ""

# Generate +2% pitch shift
echo "Creating +2% pitch shift..."
ffmpeg -i "$INPUT_FILE" -filter:a "asetrate=44100*1.02,aresample=44100" -y "$OUTPUT_DIR/${FILENAME}_plus_2.$EXTENSION" -loglevel error

# Generate +4% pitch shift
echo "Creating +4% pitch shift..."
ffmpeg -i "$INPUT_FILE" -filter:a "asetrate=44100*1.04,aresample=44100" -y "$OUTPUT_DIR/${FILENAME}_plus_4.$EXTENSION" -loglevel error

# Generate -2% pitch shift
echo "Creating -2% pitch shift..."
ffmpeg -i "$INPUT_FILE" -filter:a "asetrate=44100*0.98,aresample=44100" -y "$OUTPUT_DIR/${FILENAME}_minus_2.$EXTENSION" -loglevel error

# Generate -4% pitch shift
echo "Creating -4% pitch shift..."
ffmpeg -i "$INPUT_FILE" -filter:a "asetrate=44100*0.96,aresample=44100" -y "$OUTPUT_DIR/${FILENAME}_minus_4.$EXTENSION" -loglevel error

echo ""
echo "Successfully generated 4 pitch-shifted variants:"
echo "  - ${FILENAME}_plus_2.$EXTENSION (+2%)"
echo "  - ${FILENAME}_plus_4.$EXTENSION (+4%)"
echo "  - ${FILENAME}_minus_2.$EXTENSION (-2%)"
echo "  - ${FILENAME}_minus_4.$EXTENSION (-4%)"
echo ""
echo "All files saved to: $OUTPUT_DIR"