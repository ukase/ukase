# Generate .xslx files
## Process
1. Generate html template (according to handlebars template)
2. Load html with css as DOM object (flying saucer)
3. parse DOM to get all Tables

## Document model
Every `<table>...</table>` - becomes sheet
Table's block `<caption>some string</caption>` - becomes name of sheet

## Parsable attributes
`colspan` merge table cells by x-axis
`rowspan` merge table cells by y-axis

## Parsable CSS rules
 - border style & width:
   - `medium dashed border` - `border: 1mm dashed`
   - `medium dashed-dotted border` - `border: 1mm dotted`
   - `thick border` - `border: 1mm solid` or `border: 1mm solid` 
   - `dashed border` - `border: 0.5mm dashed`
   - `dotted border` - `border: 0.5mm dotted`
   - `medium border` - `border: 0.5mm solid`
   - `hair border` - `border: 0.1mm dotted`
   - `thin border` - `border: 0.1mm solid`
   - `none border` - `border: 0`
 - horizontal alignment (works for rows and for cells) - `text-algin: center|top|bottom|justify`
 - vertical alignment - `vertical-align: top|middle|bottom`
 - enable wrapping - `word-wrap: bold` (currently only `break-word` enables cell wrapping)
 - bold font - `font-weight: bold` (currently only `bold` enables bold font)
 - font size - `font-size: Xpt` (currently only `pt` values are known)
 - background - `background: #RRGGBB` (only hex-defined colors were tested yet)

## Known non-obvious parse features:
 - Styling for merged cells - you should style directly root cell to apply
 - on word wrapping: whitespaces (all - space/tab/new line) are make sense... it is not html actually :confused:
 